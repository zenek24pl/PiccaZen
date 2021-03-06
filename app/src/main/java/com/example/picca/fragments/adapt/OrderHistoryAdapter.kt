package com.example.picca.fragments.adapt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.picca.ActivityInteractions
import com.example.picca.R
import com.example.picca.model.BasketItem
import com.example.picca.model.Product
import com.example.picca.sharedPref.UserUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class OrderHistoryAdapter(val pizzaList: MutableList<Product>, val context: Context, val activityInteractions: ActivityInteractions) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    var db = FirebaseFirestore.getInstance()
    var id:String?=null
    var basketItem: BasketItem?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fr_order, parent, false)

        context?.let {
            id = UserUtils(it).getUserID()

        }

        return ViewHolder(
            rootView
        )
    }

    override fun getItemCount(): Int {
        return pizzaList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.pizzaName.setText(pizzaList[position].name)
        holder.pizzaDescr.setText(pizzaList[position].descr)
        holder.pizzaPrice.setText(pizzaList[position].price.toString())

        holder.add.setOnClickListener {

            basketItem?.dishes?.add(pizzaList[position].id)
            basketItem?.count="1";
            basketItem?.id= id.toString()
            basketItem?.price= pizzaList[position].price.toString()


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
