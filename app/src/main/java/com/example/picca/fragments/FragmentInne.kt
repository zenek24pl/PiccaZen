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
import com.example.picca.fragments.adapt.InneAdapter
import com.example.picca.fragments.adapt.PizzaMenuAdapter
import com.example.picca.model.Pizza
import com.example.picca.model.Product
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fr_dish.*
import kotlinx.android.synthetic.main.fr_inne.*
import kotlinx.android.synthetic.main.fr_pizza.*

class FragmentInne:BaseFragment() {

    var db = FirebaseFirestore.getInstance()
    var productList: ArrayList<Product> = arrayListOf()
    var adapter: InneAdapter? = null

    companion object {
        fun newInstance(): FragmentInne? {
            return FragmentInne()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fr_inne, container, false)
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
        adapter?.notifyDataSetChanged()

    }

    private fun setUpRecyclerView() {
        val query: Query = db.collection("inne")

        val options = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .build()
        rv_inne.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL, false)

        adapter = context?.let { actions?.let { it1 -> InneAdapter(options, it, it1) } }


        rv_inne.adapter = adapter
        adapter?.startListening()
        adapter?.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        productList.clear()
//        db.collection("inne")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    productList.add(document.toObject(Product::class.java))
//                    Log.d("OK", "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d("NieOK", "Error getting documents: ", exception)
//            }
//            .addOnCompleteListener {
//                rv_inne.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
//                rv_inne.adapter =
//                    actions?.let { it1 ->
//                        activity?.applicationContext?.let { it2 ->
//                            InneAdapter(
//                                productList, it2,
//                                it1
//                            )
//                        }
//                    }
//            }

    setUpRecyclerView()
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