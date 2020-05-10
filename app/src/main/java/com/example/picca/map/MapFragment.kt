package com.example.picca.map

import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.cursoradapter.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import com.example.picca.ActivityInteractions
import com.example.picca.BaseFragment
import com.example.picca.R
import com.example.picca.fragments.PagerFragment
import com.example.picca.model.PizzaPlace
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.android.synthetic.main.info_window_map.view.*
import kotlinx.android.synthetic.main.map_lay.*


class MapFragment :
    BaseFragment(),
    OnMapReadyCallback {

    val POLAND_LAT_LNG = LatLng(51.759465, 19.457760)
    val ZOOM_LEVEL = 13f
    var googleMap: GoogleMap? = null
    val TAG_CODE_PERMISSION_LOCATION = 0
    var cursorAdapter: SimpleCursorAdapter? = null
    var clusterManager: ClusterManager<PizzaPlace>? = null

    companion object {
        fun newInstance(): MapFragment? {
            return MapFragment()
        }
    }

    var db = FirebaseFirestore.getInstance()
    var placeList: ArrayList<PizzaPlace> = arrayListOf()
    var placeNames: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.map_lay, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_v.isSubmitButtonEnabled = true
        search_v.queryHint = "Podaj nazwę restauracji lub znajdź na mapie"
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)

        search_v.isIconified = false
        cursorAdapter = SimpleCursorAdapter(
            context, R.layout.search_item, null, from,
            intArrayOf(R.id.item_label), FLAG_REGISTER_CONTENT_OBSERVER
        )
        search_v.suggestionsAdapter = cursorAdapter


        val mapFragment: SupportMapFragment? =
            childFragmentManager?.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        actions?.topBar()?.showTopBar(true)
        hideKeyboard()
    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just move the camera to Sydney and add a marker in Sydney.
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        googleMap ?: return
        with(googleMap) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setTiltGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.setBuildingsEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(POLAND_LAT_LNG, 6f))
            getMarkersData()
            setSearchEn()
        }
    }

    private fun setSearchEn() {
        search_v.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                var cord = placeList.find { it.name == query }
                googleMap?.moveCamera(
                    CameraUpdateFactory
                        .newLatLngZoom(cord?.geoLat?.toDouble()?.let {
                            LatLng(
                                it,
                                cord?.geoLng?.toDouble()
                            )
                        }, 10f)
                )

                clusterManager?.markerCollection?.markers?.find { it.title == query }
                    ?.showInfoWindow()
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                val cursor =
                    MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                query?.let {
                    placeNames.forEachIndexed { index, suggestion ->
                        if (suggestion.contains(query, true))
                            cursor.addRow(arrayOf(index, suggestion))
                    }
                }

                cursorAdapter?.changeCursor(cursor)
                return true
            }
        })

        search_v.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                hideKeyboard()
                val cursor = search_v.suggestionsAdapter.getItem(position) as Cursor
                val selection =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                search_v.setQuery(selection, false)

                // Do something with selection
                return true
            }

        })
    }

    private fun getMarkersData() {
        db.collection("pizzas_place")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    placeList.add(document.toObject(PizzaPlace::class.java))
                    Log.d("OK", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NieOK", "Error getting documents: ", exception)
            }
            .addOnCompleteListener {
                placeList.forEach {
                    placeNames.add(it.name)
                }
                setCustomInfoWindow()
            }

    }

    fun Fragment.hideKeyboard() {
        view?.let {
            activity?.hideKeyboard(it)
        }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setCustomInfoWindow() {

        clusterManager = ClusterManager(this.context, googleMap) // 1

        var renderer: OwnIconRendered? =
            context?.let { OwnIconRendered(it, googleMap, clusterManager!!) }

        clusterManager!!.setRenderer(renderer)
        googleMap?.setInfoWindowAdapter(clusterManager?.getMarkerManager()) // 3

        clusterManager!!.getMarkerCollection()
            .setOnInfoWindowAdapter(context?.let {
                actions?.let { it1 ->
                    CustomMarkerInfoWindowView(
                        it, placeList,
                        it1
                    )
                }
            }) // 4

        googleMap?.setOnMarkerClickListener(clusterManager);
        clusterManager!!.setOnClusterItemInfoWindowClickListener {
            actions?.navigateTo(PagerFragment.newInstance(it.name), true)

        }
        googleMap?.setOnInfoWindowClickListener(clusterManager);
        googleMap?.setOnCameraIdleListener(clusterManager)
        context?.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                googleMap?.setMyLocationEnabled(true);
                googleMap?.getUiSettings()?.setMyLocationButtonEnabled(true);
            } else {
                Toast.makeText(context, "Potrzeba pozwolić na lokalizacje", Toast.LENGTH_LONG)
                    .show();

                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    TAG_CODE_PERMISSION_LOCATION
                )

            }
        }
        clusterManager!!.addItems(placeList)
        clusterManager!!.cluster()

    }

    class CustomMarkerInfoWindowView(
        val context: Context,
        var placeList: List<PizzaPlace>,
        var activityInteractions: ActivityInteractions
    ) : GoogleMap.InfoWindowAdapter {
        override fun getInfoContents(p0: Marker?): View {
            var mInfoView =
                (context as Activity).layoutInflater.inflate(R.layout.info_window_map, null)
            var data = placeList.find { it.name == p0?.title ?: "" }
            if (data != null) {
                mInfoView.placeName.setText(data?.name)
                mInfoView.placeAdress.setText(data.addressStreet + " " + data.addressStreetNo)
                mInfoView.openHour.setText("Czynne od :" + data.openHours + "do " + data.openTo)
                mInfoView.phoneNumb.setText("Tel: " + data.phoneNo)
                mInfoView.goToPlace.setOnClickListener {
                }
                mInfoView.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ph))



            }
            return mInfoView

        }

        override fun getInfoWindow(p0: Marker?): View? {
            return null
        }


    }
}

class OwnIconRendered(
    val context: Context,
    googleMap: GoogleMap?,
    clusterManager: ClusterManager<PizzaPlace>
) : DefaultClusterRenderer<PizzaPlace>(context, googleMap, clusterManager) {
    override fun onBeforeClusterItemRendered(item: PizzaPlace?, markerOptions: MarkerOptions?) {
var height = 100;
var width = 100;
var bitmapdraw = ContextCompat.getDrawable(context,R.drawable.poi_rest)?.toBitmap(100,100);
        markerOptions?.icon(BitmapDescriptorFactory.fromBitmap(bitmapdraw))

        markerOptions?.snippet(item?.name);
        markerOptions?.title(item?.name);
        super.onBeforeClusterItemRendered(item, markerOptions)
    }


}
