package com.example.picca.fragments.adapt

import android.content.Context
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
import com.example.picca.model.Pizza
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException


class PizzaMenuAdapter(val pizzaList: FirestoreRecyclerOptions<Pizza>, val context: Context, val activityInteractions: ActivityInteractions) :
    FirestoreRecyclerAdapter<Pizza, PizzaMenuAdapter.ViewHolder>(pizzaList) {

    var db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.pizza_item, parent, false)

        return ViewHolder(
            rootView
        )
    }
    override fun onDataChanged() { // Called each time there is a new query snapshot. You may want to use this method
// to hide a loading spinner or check for the "no documents" state and update your UI.
// ...
        System.out.println("DD")
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model:Pizza) {
        holder.pizzaName.setText(model.name)
        holder.pizzaDescr.setText(model.descr)
        holder.pizzaPrice.setText(model.price.toString())

        holder.add.setOnClickListener {
        activityInteractions.navigateTo(  ExtrasDialog.newInstance(model.id),true)
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

