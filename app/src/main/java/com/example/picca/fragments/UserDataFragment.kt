package com.example.picca.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picca.BaseFragment
import com.example.picca.MainActivity
import com.example.picca.R
import com.example.picca.fragments.adapt.DishAdapter
import com.example.picca.model.Adress
import com.example.picca.model.Product
import com.example.picca.model.User
import com.example.picca.sharedPref.UserUtils
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.fr_basket.*
import kotlinx.android.synthetic.main.fr_user_data.*

class UserDataFragment: BaseFragment() {

    var user:User?=null
    var adress:Adress?= Adress()
    var id:String?=""
companion object {
    fun newInstance(): UserDataFragment? {
        return UserDataFragment()
    }
} var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fr_user_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
          context?.let {
              id= UserUtils(it).getUserID()

         }


        db.collection("users")
            .whereEqualTo(FieldPath.documentId(), context?.let { id})
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    user=document.toObject(User::class.java)
                    Log.d("OK", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NieOK", "Error getting documents: ", exception)
            }
            .addOnCompleteListener{
                user_name.setText(user?.email)
                user_pass.setText(user?.password)

                    db.collection("adress")
                        .whereEqualTo(FieldPath.documentId(), id)
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                adress = document.toObject(Adress::class.java)
                                Log.d("OK", "${document.id} => ${document.data}")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("NieOK", "Error getting documents: ", exception)
                        }
                        .addOnCompleteListener {
                            user_street.setText(adress?.street)
                            user_number.setText(adress?.hauseNumber)
                            user_postcode.setText(adress?.postCode)
                            user_phone.setText(adress?.phone)

                        }

                tv_points_val.setText(user?.points)

            }



        user_points.setOnClickListener {

            adress?.hauseNumber=user_number.text.toString()
            adress?.phone=user_phone.editableText.toString()
            adress?.street=user_street.editableText.toString()
            adress?.postCode=user_postcode.editableText.toString()

            id?.let { it1 ->
                adress?.let { it2 ->
                    db.collection("adress")
                        .document(it1)
                        .set(it2, SetOptions.merge())


                }
            }

            user_popup.text = "OK!"

        }

    }
}