package com.example.picca.fragments.adapt

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.picca.ActivityInteractions
import com.example.picca.R
import com.example.picca.model.BasketItem
import com.example.picca.model.Pizza
import com.example.picca.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class BasketAdapter(
    val pizzaList: MutableList<BasketItem>,
    val context: Context,
    val activityInteractions: ActivityInteractions
) :
    RecyclerView.Adapter<BasketAdapter.ViewHolder>() {

    var db = FirebaseFirestore.getInstance()
    var pizza: Pizza? = null
    var product: Product? = null
    var onBasketAction:OnBasketAction?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.basket_item, parent, false)

        return ViewHolder(
            rootView
        )
    }

    override fun getItemCount(): Int {
        return pizzaList.size
    }

    fun getPizzaData(
        id: String,
        holder: ViewHolder
    ) {

        db.collection("pizza").document(id)
            .get()

            .addOnSuccessListener { result ->
                if (result.exists()) {
                    pizza = result.toObject(Pizza::class.java)
                    holder.pizzaName.setText(pizza?.name)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NieOK", "Error getting documents: ", exception)
            }

    }

    fun getDishData(
        id: String,
        holder: ViewHolder
    ) {

        db.collection("products").document(id)
            .get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    product = result.toObject(Product::class.java)
                    holder.pizzaName.setText(product?.name)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NieOK", "Error getting documents: ", exception)
            }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        var data = pizzaList[position]

        if (!data.pizza.isNullOrEmpty()) {
            getPizzaData(data.pizza.first(),holder)

            holder.pizzaDescr.setText("sos " + pizzaList[position].souce + " ciasto " + data.plate + " rozmiar " + data.pizzaSize)
        } else {
            getDishData(data.dishes.first(),holder)


            holder.pizzaDescr.setText(product?.name)
        }
        holder.pizzaPrice.setText(pizzaList[position].price.toString())
        holder.count.text = data.count
        holder.delete.setOnClickListener {
            if( data.count.isEmpty() ||data.count.toInt()==1 ){
                db.collection("basket").document(data.basketID)
                    .delete()
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                onBasketAction?.onDelete(data)
                pizzaList.remove(data)
            }else{
                var count=
                    (data.count.toInt()-1)
                data.count=count.toString();
                db.collection("basket").document(data.basketID)
                    .set(data, SetOptions.merge())
                onBasketAction?.onDelete(data)
            }
            notifyDataSetChanged()

        }
        holder.add.setOnClickListener {
            var count:Int
            if(data.count.isEmpty()){
                count=1;
            }else {
              count= (data.count.toInt() + 1)
            }
            data.count = count.toString();

            db.collection("basket").document(data.basketID)
                .set(data, SetOptions.merge())
            onBasketAction?.onAdd(data)
            notifyDataSetChanged()
        }


    }

    class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView),
        View.OnClickListener {
        val pizzaName: TextView
        val pizzaDescr: TextView
        val pizzaPrice: TextView
        val count:TextView
        val delete: Button
        val add: Button

        override fun onClick(view: View) {

        }

        init {
            pizzaName = rootView.findViewById(R.id.tvName)
            pizzaDescr = rootView.findViewById(R.id.tvDescr)
            pizzaPrice = rootView.findViewById(R.id.price_m)
            delete = rootView.findViewById(R.id.delete)
            count=rootView.findViewById(R.id.quantity)
            add = rootView.findViewById(R.id.add)
        }
    }


}
interface OnBasketAction{
    fun onDelete(basketItem: BasketItem)
    fun onAdd(basketItem: BasketItem)
}