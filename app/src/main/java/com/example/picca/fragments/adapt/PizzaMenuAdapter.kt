package com.example.picca.fragments.adapt

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.picca.ActivityInteractions
import com.example.picca.R
import com.example.picca.fragments.ExtrasDialog
import com.example.picca.fragments.OrderFragment
import com.example.picca.model.Pizza
import com.example.picca.sharedPref.UserUtils
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PizzaMenuAdapter(val pizzaList: MutableList<Pizza>,val context: Context,val activityInteractions: ActivityInteractions) :
    RecyclerView.Adapter<PizzaMenuAdapter.ViewHolder>() {

    var db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.pizza_item, parent, false)

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
        activityInteractions.navigateTo(  ExtrasDialog.newInstance(pizzaList[position].id),true)
        }


    }
    class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView),
        View.OnClickListener {
         val pizzaName: TextView
         val pizzaDescr: TextView
         val pizzaPrice: TextView
         val pizzaImg:ImageView

         val add:Button

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

