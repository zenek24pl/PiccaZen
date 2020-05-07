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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fr_dish.*
import kotlinx.android.synthetic.main.fr_inne.*
import kotlinx.android.synthetic.main.fr_pizza.*

class FragmentInne:BaseFragment() {

    var db = FirebaseFirestore.getInstance()
    var productList:ArrayList<Product> = arrayListOf()

    companion object{
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
        productList.clear()
        db.collection("inne")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    productList.add(document.toObject(Product::class.java))
                    Log.d("OK", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NieOK", "Error getting documents: ", exception)
            }
            .addOnCompleteListener {
                rv_inne.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                rv_inne.adapter =
                    actions?.let { it1 ->
                        activity?.applicationContext?.let { it2 ->
                            InneAdapter(
                                productList, it2,
                                it1
                            )
                        }
                    }
            }






    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productList.clear()
        db.collection("inne")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    productList.add(document.toObject(Product::class.java))
                    Log.d("OK", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NieOK", "Error getting documents: ", exception)
            }
            .addOnCompleteListener {
                rv_inne.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                rv_inne.adapter =
                    actions?.let { it1 ->
                        activity?.applicationContext?.let { it2 ->
                            InneAdapter(
                                productList, it2,
                                it1
                            )
                        }
                    }
            }


    }}