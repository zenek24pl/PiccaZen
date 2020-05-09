package com.example.picca.fragments.adapt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.picca.ActivityInteractions
import com.example.picca.R
import com.example.picca.model.BasketItem
import com.example.picca.model.Ingredients
import com.example.picca.model.Pizza
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ExtrasAdapter(val ingredients: FirestoreRecyclerOptions<Ingredients>, val context: Context, val activityInteractions: ActivityInteractions, val pizza:Pizza) :
FirestoreRecyclerAdapter<Ingredients, ExtrasAdapter.ExtrasViewHolder>(ingredients)
{
   var ingList:ArrayList<String> = arrayListOf()


                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtrasViewHolder {
        val rootView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_extras_item, parent, false)

        return ExtrasViewHolder(
            rootView
        )
        }



    override fun onBindViewHolder(holder: ExtrasViewHolder, position: Int,model:Ingredients) {
        holder.checkBox.text = model.name
        pizza.ingr.forEach {
            if (it==model.id){
                holder.checkBox.isChecked=true
            }
        }



//        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
//            run {
//                if (isChecked) {
//                    ingList.add(ingredients[position].id)
//                }else{
//                    ingList.remove(ingredients[position].id)
//                }
//
//            }
//        }
     }


        class ExtrasViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView),
    View.OnClickListener {
        val checkBox: CheckBox


        override fun onClick(view: View) {

        }

        init {
            checkBox= rootView.findViewById(R.id.info_text)


        }
    }



}



