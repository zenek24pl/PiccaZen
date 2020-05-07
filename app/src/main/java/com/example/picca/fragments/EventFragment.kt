package com.example.picca.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.picca.BaseFragment
import com.example.picca.R

class EventFragment:BaseFragment() {

    companion object{
        fun newInstance(): EventFragment? {
            return EventFragment()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fr_event, container, false)
    }

}