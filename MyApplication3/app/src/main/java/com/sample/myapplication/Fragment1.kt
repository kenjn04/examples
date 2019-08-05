package com.sample.myapplication

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment1.*

class Fragment1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text1.setOnClickListener {
            val manager = activity!!.supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.replace(id, Fragment2.newInstance())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = Fragment1()
    }
}
