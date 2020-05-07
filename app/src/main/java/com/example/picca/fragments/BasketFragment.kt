package com.example.picca.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picca.BaseFragment
import com.example.picca.R
import com.example.picca.fragments.adapt.BasketAdapter
import com.example.picca.fragments.adapt.OnBasketAction
import com.example.picca.model.BasketItem
import com.example.picca.sharedPref.UserUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fr_basket.*

class BasketFragment : BaseFragment(), OnBasketAction {

    var db = FirebaseFirestore.getInstance()
    var productList: ArrayList<BasketItem> = arrayListOf()
    var id: String? = ""
    var orderList: ArrayList<String> = arrayListOf()
    var sum=0;
    companion object {
        fun newInstance(): BasketFragment? {
            return BasketFragment()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fr_basket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            id = UserUtils(it).getUserID()

        }
        refreshData()
        bt_order.setOnClickListener {

            actions?.navigateTo(OrderFragment.newInstance(sum), false)


        }

        setPrice()
    }

    private fun setPrice() {

        productList.forEach {

            sum+=it.price.toInt()*it.count.toInt()
        }

        tv_bill.setText(sum.toString())
        tv_delivery.setText("0zł")
        tvSum.setText(sum.toString())
    }

    fun refreshData() {
        productList.clear()
        db.collection("basket")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    productList.add(document.toObject(BasketItem::class.java))
                    Log.d("OK", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NieOK", "Error getting documents: ", exception)
            }
            .addOnCompleteListener {
                rv_basket.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                rv_basket.adapter =
                    actions?.let { it1 ->
                        activity?.applicationContext?.let { it2 ->
                            BasketAdapter(
                                productList, it2,
                                it1
                            )
                        }
                    }
            }
    }

    override fun onDelete(basketItem: BasketItem) {
        refreshData()
    }

    override fun onAdd(basketItem: BasketItem) {
        refreshData()
    }


}