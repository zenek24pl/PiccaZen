package com.example.picca.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.picca.BaseFragment
import com.example.picca.R
import kotlinx.android.synthetic.main.pager_layout.*

class PagerFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pager_layout,container,false)
    }
    companion object{
        private const val NAME = "NAME"
        fun newInstance(name: String): PagerFragment? {
            var pf:PagerFragment= PagerFragment()
            var args=Bundle()
            args.putString(NAME,name)
            pf.arguments=args
            return pf
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentAdapter = fragmentManager?.let { MyPagerAdapter(it) }
        viewPager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)
        actions?.topBar()?.showTopBar(true)
        actions?.topBar()?.setTitle(arguments?.getString(NAME))
    }
}

class MyPagerAdapter(supportFragmentManager: FragmentManager): FragmentPagerAdapter(supportFragmentManager) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                FragmentPizza.newInstance() as BaseFragment
            }
            1 -> FragmentDish.newInstance() as BaseFragment
            else -> {
                return FragmentInne.newInstance() as BaseFragment
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Pizza"
            1 -> "Dania"
            else -> {
                return "Dodatki"
            }
        }
    }

}
