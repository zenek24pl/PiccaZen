package com.example.picca.fragments.adapt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.picca.ActivityInteractions
import com.example.picca.R
import com.example.picca.model.Ingredients
import com.example.picca.model.Pizza

class ExtrasAdapter(val ingredients: MutableList<Ingredients>, val context: Context, val activityInteractions: ActivityInteractions,val pizza:Pizza) :
    RecyclerView.Adapter<ExtrasAdapter.ExtrasViewHolder>() {
   var ingList:ArrayList<String> = arrayListOf()


                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtrasViewHolder {
        val rootView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_extras_item, parent, false)

        return ExtrasViewHolder(
            rootView
        )
        }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    override fun onBindViewHolder(holder: ExtrasViewHolder, position: Int) {
        holder.checkBox.text = ingredients[position].name
        pizza.ingr.forEach {
            if (it==ingredients[position].id){
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



