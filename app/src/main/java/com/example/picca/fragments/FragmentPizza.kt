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
import com.example.picca.fragments.adapt.PizzaMenuAdapter
import com.example.picca.model.Pizza
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fr_dish.*
import kotlinx.android.synthetic.main.fr_pizza.*

class FragmentPizza : BaseFragment() {

    var db = FirebaseFirestore.getInstance()
    var productList: ArrayList<Pizza> = arrayListOf()

    companion object {
        fun newInstance(): FragmentPizza? {
            return FragmentPizza()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fr_pizza, container, false)
    }

    override fun onResume() {
        super.onResume()
        productList.clear()
        db.collection("pizza")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var item=document.toObject(Pizza::class.java)
                    item.id=document.id
                    productList.add(item)
                    Log.d("OK", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NieOK", "Error getting documents: ", exception)
            }
            .addOnCompleteListener {
                rv_pizza.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                rv_pizza.adapter =
                    actions?.let { it1 ->
                        activity?.applicationContext?.let { it2 ->
                            PizzaMenuAdapter(
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
        db.collection("pizza")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var item=document.toObject(Pizza::class.java)
                    item.id=document.id
                    productList.add(item)
                    Log.d("OK", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NieOK", "Error getting documents: ", exception)
            }
            .addOnCompleteListener {
                rv_pizza.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                rv_pizza.adapter =
                    actions?.let { it1 ->
                        activity?.applicationContext?.let { it2 ->
                            PizzaMenuAdapter(
                                productList, it2,
                                it1
                            )
                        }
                    }
            }

    }
}