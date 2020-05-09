package com.example.picca.fragments.adapt

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.picca.ActivityInteractions
import com.example.picca.GlideApp
import com.example.picca.R
import com.example.picca.fragments.ExtrasDialog
import com.example.picca.fragments.OrderFragment
import com.example.picca.model.BasketItem
import com.example.picca.model.Product
import com.example.picca.sharedPref.UserUtils
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class InneAdapter(val pizzaList: FirestoreRecyclerOptions<Product>, val context: Context, val activityInteractions: ActivityInteractions) :
    FirestoreRecyclerAdapter<Product, InneAdapter.ViewHolder>(pizzaList) {

    var db = FirebaseFirestore.getInstance()
    var id:String?=null
    var basketItem:BasketItem?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.dish_item, parent, false)

        context?.let {
            id = UserUtils(it).getUserID()

        }

        return ViewHolder(
            rootView
        )
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int,model:Product) {
        holder.pizzaName.setText(model.name)
        holder.pizzaDescr.setText(model.descr)
        holder.pizzaPrice.setText(model.price.toString())
        GlideApp.with(context).load(model.img)
            .fitCenter()
            .into(holder.pizzaImg)
        holder.add.setOnClickListener {
            basketItem?.dishes?.add(model.id)
            basketItem?.count="1";
            basketItem?.id= id.toString()
            basketItem?.price= model.price.toString()

            basketItem?.let { it1 ->
                db.collection("basket")
                    .add(it1)
                    .addOnSuccessListener(OnSuccessListener<DocumentReference> { documentReference ->
                        var bID = documentReference.id
                        basketItem?.basketID = bID
                        db.collection("basket").document(documentReference.id)
                            .set(it1)
                    })

                    .addOnFailureListener(OnFailureListener { e ->

                    }).addOnCompleteListener(OnCompleteListener {
                        Toast.makeText(context,"Dodano pomyślnie do koszyka",Toast.LENGTH_SHORT).show()
                    })
            }

        }


    }
    class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView),
        View.OnClickListener {
        val pizzaName: TextView
        val pizzaDescr: TextView
        val pizzaPrice: TextView
        val pizzaImg: ImageView

        val add: Button

        override fun onClick(view: View) {

        }

        init {
            pizzaName= rootView.findViewById(R.id.tvName)
            pizzaDescr=rootView.findViewById(R.id.tvDescr)
            pizzaImg=rootView.findViewById(R.id.imgPizza)
            pizzaPrice=rootView.findViewById(R.id.price_m)
            add=rootView.findViewById(R.id.add)

        }
    }



}
