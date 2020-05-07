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
import com.example.picca.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fr_dish.*

class FragmentDish: BaseFragment() {
    var db = FirebaseFirestore.getInstance()
    var productList:ArrayList<Product> = arrayListOf()
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
        productList.clear()
        db.collection("products")
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
            .addOnCompleteListener{
                rv_dish.layoutManager= LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                rv_dish.adapter=
                    actions?.let { it1 ->
                        activity?.applicationContext?.let { it2 ->
                            DishAdapter(
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
        db.collection("products")
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
            .addOnCompleteListener{
                rv_dish.layoutManager= LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                rv_dish.adapter=
                    actions?.let { it1 ->
                        activity?.applicationContext?.let { it2 ->
                            DishAdapter(
                                productList, it2,
                                it1
                            )
                        }
                    }
            }



        }

}


