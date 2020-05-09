package com.example.picca.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picca.BaseFragment
import com.example.picca.R
import com.example.picca.fragments.adapt.BasketAdapter
import com.example.picca.model.Adress
import com.example.picca.model.BasketItem
import com.example.picca.model.Order
import com.example.picca.model.User
import com.example.picca.sharedPref.UserUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.paypal.android.sdk.payments.*
import kotlinx.android.synthetic.main.fr_basket.*
import kotlinx.android.synthetic.main.fr_basket.tvSum
import kotlinx.android.synthetic.main.fr_basket.tv_bill
import kotlinx.android.synthetic.main.fr_order.*
import kotlinx.android.synthetic.main.fr_order.user_name
import kotlinx.android.synthetic.main.fr_order.user_number
import kotlinx.android.synthetic.main.fr_order.user_phone
import kotlinx.android.synthetic.main.fr_order.user_postcode
import kotlinx.android.synthetic.main.fr_order.user_street
import kotlinx.android.synthetic.main.fr_user_data.*
import org.json.JSONException
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

private val config =
    PayPalConfiguration() // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
// or live (ENVIRONMENT_PRODUCTION)
        .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
        .clientId("<YOUR_CLIENT_ID>")
class OrderFragment: BaseFragment() {


    var db = FirebaseFirestore.getInstance()
    var user:User?=null
    var adress:Adress?=null
    companion object{
        private val ID: String="ID"

        fun newInstance(sum: Int): OrderFragment? {
            var pf:OrderFragment= OrderFragment()
            var args=Bundle()
            args.putInt(ID,sum)
            pf.arguments=args
            return pf
        }

    }
    var id:String?=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fr_order, container, false)
    }



    override fun onDestroy() {
        activity?.stopService(Intent(context, PayPalService::class.java))
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(context, PayPalService::class.java)

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        activity?.startService(intent)

        context?.let {
            id = UserUtils(it).getUserID()

        }

        arguments?.getInt(ID)?.let { tvSum.setText(it.toString()) }

        checkChangesOnKupon()
        db.collection("users")
            .whereEqualTo(FieldPath.documentId(), context?.let { id })
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    user = document.toObject(User::class.java)
                    Log.d("OK", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NieOK", "Error getting documents: ", exception)
            }
            .addOnCompleteListener {
                user_name.setText(user?.email)

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


            }

        kupon_val.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkChangesOnKupon()
            }

        })
        bt_orderAll.setOnClickListener {
            if(adress==null && user_street.text.isNullOrEmpty() ||
                    user_number.text.isNullOrEmpty() ||
                    user_postcode.text.isNullOrEmpty() ||
                    user_phone.text.isNullOrEmpty()
                        ){
                    Toast.makeText(context,"Uzupełnij adres w celu zfinalizowania zamówienia",Toast.LENGTH_SHORT).show()

            }
            else if(adress !=null && (adress?.street.isNullOrEmpty() || adress?.hauseNumber.isNullOrEmpty() ||
                        adress?.phone.isNullOrEmpty() || adress?.postCode.isNullOrEmpty())
            ){
                Toast.makeText(context,"Uzupełnij adres w celu zfinalizowania zamówienia",Toast.LENGTH_SHORT).show()

            }else {


                if (radioGroup.checkedRadioButtonId == -1) {
                    Toast.makeText(
                        context,
                        "Wybierz formę zapłaty aby kontynuować",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (odbior.isChecked) {
                        setOrder(false)
                        AlertDialog.Builder(context)
                            .setTitle("Dziękujemy zamówienie nr 212314 jest już w drodze")
                            .setMessage("") // Specifying a listener allows you to take an action before dismissing the dialog.
// The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(
                                "Przejście do historii zamówień",
                                DialogInterface.OnClickListener { dialog, which ->
                                    actions?.navigateTo(OrderHistory.newInstance(), true)
                                }) // A null listener allows the button to dismiss the dialog and take no further action.

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show()


                    } else if (paypal.isChecked) {
                        setOrder(true)
                        onBuyPressed()
                    }
                }
            }

        }


    }

    private fun setOrder(isPaypal:Boolean) {
        val productList:ArrayList<BasketItem> = arrayListOf()

        var order:Order?=null
        order?.finalPrice=tvSum.text.toString()
        order?.adress= adress?.id.toString()
        val r = Random()
        val i1: Int = r.nextInt(8000 - 100) + 100

        order?.orderNumber=i1.toString()
        if(isPaypal) {
            order?.paymentMethod ="paypal"
        }else{
            order?.paymentMethod ="przy odbiorze"
        }




        productList.forEach {
            order?.listOfOrders?.add(it.id)
        }


        order?.let {
            db.collection("order")
                .add(it)
                .addOnSuccessListener(OnSuccessListener<DocumentReference> { documentReference ->
                    var bID=documentReference.id
                    order?.id=bID
                    db.collection("order").document(documentReference.id)
                        .set(order)
                })
                .addOnFailureListener(OnFailureListener { e ->

                }).addOnCompleteListener(OnCompleteListener {

                })
        }



    }

    fun checkChangesOnKupon(){
        if(kupon_val.editableText.toString()=="Andzej"){
            tv_bill.setText("10")
            var value=0;
            arguments?.getInt(ID)?.let {
                value=it -10
                tvSum.setText(value.toString()) }
        }
        else{
            tv_bill.text = "0"
            arguments?.getInt(ID)?.let {

                tvSum.setText(it.toString()) }
        }
    }

    fun onBuyPressed() { // PAYMENT_INTENT_SALE will cause the payment to complete immediately.

        val payment = PayPalPayment(
            BigDecimal("1.75"), "USD", "hipster jeans",
            PayPalPayment.PAYMENT_INTENT_SALE
        )
        val intent = Intent(context, PaymentActivity::class.java)
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
        startActivityForResult(intent, 0)
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {
        if (resultCode == Activity.RESULT_OK) {
            val confirm: PaymentConfirmation =
                data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4))
                    // TODO: send 'confirm' to your server for verification.
                    Toast.makeText(context,"Platnosc za spodnie przeszla prawidłowo :D gz",Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e)
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.")
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i(
                "paymentExample",
                "An invalid Payment or PayPalConfiguration was submitted. Please see the docs."
            )
        }
    }
}