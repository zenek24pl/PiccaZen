package com.example.picca.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picca.BaseFragment
import com.example.picca.R
import com.example.picca.fragments.adapt.ExtrasAdapter
import com.example.picca.model.*
import com.example.picca.sharedPref.UserUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dodatki_layout.*

class ExtrasDialog: BaseFragment() {

    var checked:Checked= Checked()
    var db = FirebaseFirestore.getInstance()
    var ingredients:ArrayList<Ingredients> = arrayListOf()
    var sum=0;
    var basketItem:BasketItem= BasketItem()
    var p:Pizza= Pizza()
    var id:String?=""
    companion object{
        const val ID:String = "ID"
        fun newInstance(id: String): ExtrasDialog? {
            var pf:ExtrasDialog= ExtrasDialog()
            var args=Bundle()
            args.putString(ExtrasDialog.ID,id)
            pf.arguments=args
            return pf
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(
            R.layout.dodatki_layout, container,
            false
        )

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            id= UserUtils(it).getUserID()

        }
        db.collection("pizza").document(arguments?.getString(ID).toString())
            .get()
            .addOnSuccessListener {
                if(it.exists()){
                    p= it.toObject(Pizza::class.java)!!
                    p.id=it.id
                }

            }




        db.collection("ingr")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var ingrrr=Ingredients()
                    ingrrr=document.toObject(Ingredients::class.java)
                    ingrrr.id=document.id

                    ingredients.add(ingrrr)
                    Log.d("OK", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NieOK", "Error getting documents: ", exception)
            }
            .addOnCompleteListener{
                ingr_rv.layoutManager= GridLayoutManager(context, 3,RecyclerView.VERTICAL, false)
                ingr_rv.adapter=
                    actions?.let { it1 ->
                        activity?.applicationContext?.let { it2 ->
                            ExtrasAdapter(
                                ingredients, it2,
                                it1,p
                            )
                        }
                    }
            }
        onClicksEtc()
    }

    private fun onClicksEtc() {

        radioGr_size.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.mala->{
                    checked.setOffRest()
                    checked.mala=true
                }
                R.id.duza->{
                    checked.setOffRest()
                    checked.duza=true
                }
                R.id.srednia->{
                    checked.setOffRest()
                    checked.srednia=true
                }
            }

        }
        ciasto_rg.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.normal->{
                    checked.setOffCiasto()
                    checked.normalna=true
                }
                R.id.thin->{
                    checked.setOffCiasto()
                    checked.cienka=true
                }
            }
         }
        souce_rg.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId){
            R.id.garlic->{
                checked.setOfSouce()
                checked.czosnek=true
            }
            R.id.hot->{
                checked.setOfSouce()
                checked.ostry=true
            }
            R.id.light->{
                checked.setOfSouce()
                checked.lagodny=true
            }
            }
        }

        add_to_basket.setOnClickListener {
            basketItem.pizza.add(p.id)
            basketItem.souce=checked.returnSouceId()
            basketItem.pizzaSize=checked.returnSize()
            basketItem.plate=checked.returnCiasto()
            basketItem.count="1";
            basketItem.id= id.toString()
            basketItem.price= (p.price+checked.priceSum).toString()




            db.collection("basket")
                .add(basketItem)

                .addOnSuccessListener(OnSuccessListener<DocumentReference> { documentReference ->
                    var bID=documentReference.id
                    basketItem.basketID=bID
                    db.collection("basket").document(documentReference.id)
                        .set(basketItem)
                })
                .addOnFailureListener(OnFailureListener { e ->

                }).addOnCompleteListener(OnCompleteListener {

                })



        }
    }
    class Checked{
        var mala:Boolean=false;
        var duza:Boolean=false
        var srednia:Boolean=false
        var normalna:Boolean=false;
        var cienka:Boolean=false;
        var czosnek:Boolean=false
        var lagodny:Boolean=false
        var ostry:Boolean=false

        var priceSum:Double=0.0

        fun returnCiasto():String{
            if(normalna){
                return "normalne"
            }else{
                priceSum+=2
                return "cienkie"
            }

        }
        fun returnSouceId():String{
            if(ostry){
                return "ostry"
            }else if(lagodny){
                return "lagodny"
            }else if(czosnek){
                return "czosnek"
            }
            return ""
        }
        fun returnSize():String{
            if(mala){
                return "mala"
            }else if(duza){
                priceSum+=10
                return "duza"

            }else if(srednia){
                priceSum+=5
                return "srednia"
            }
            return ""

        }

        fun setOffRest(){
            mala=false
            duza=false
            srednia=false

        }
        fun setOffCiasto(){
            normalna=false
            cienka=false
        }
        fun setOfSouce(){
            czosnek=false
            lagodny=false
            ostry=false
        }

    }
}