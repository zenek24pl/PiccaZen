package com.example.picca.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picca.BaseFragment
import com.example.picca.R
import com.example.picca.fragments.adapt.DishAdapter
import com.example.picca.fragments.adapt.ExtrasAdapter
import com.example.picca.model.Ingredients
import com.example.picca.model.Pizza
import com.example.picca.model.Product
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.dodatki_layout.*
import kotlinx.android.synthetic.main.fr_dish.*

class FragmentDish: BaseFragment() {
    var db = FirebaseFirestore.getInstance()
    var productList:ArrayList<Product> = arrayListOf()
    var adapter:DishAdapter?=null

    companion object{
        fun newInstance(): FragmentDish? {
            return FragmentDish()
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fr_dish, container, false)
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
        adapter?.notifyDataSetChanged()
     }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView();

        }
    private fun setUpRecyclerView() {
        val query: Query = db.collection("products")
        val options = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .setLifecycleOwner(this)
            .build()
        rv_dish.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL, false)

        adapter= context?.let { actions?.let { it1 -> DishAdapter(options, it, it1) } }


        rv_dish.adapter = adapter
        adapter?.startListening()
        adapter?.notifyDataSetChanged()
    }
    override fun onStart() {
        super.onStart()
        adapter?.startListening()
        adapter?.notifyDataSetChanged()

    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
        adapter?.notifyDataSetChanged()

    }
}


