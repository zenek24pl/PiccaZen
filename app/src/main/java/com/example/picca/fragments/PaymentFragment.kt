package com.example.picca.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.picca.BaseFragment
import com.example.picca.R
import com.paypal.android.sdk.payments.*
import org.json.JSONException
import java.math.BigDecimal



class PaymentFragment:BaseFragment() {

    companion object {
        private val ID: String = "ID"

        fun newInstance(sum: String): PaymentFragment? {
            var pf: PaymentFragment = PaymentFragment()
            var args = Bundle()
            args.putString(ID, sum)
            pf.arguments = args
            return pf
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.pager_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }




}