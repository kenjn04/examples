package com.example.hmi.audio.view.adapter

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.hmi.audio.common.Element
import com.example.hmi.audio.common.Track
import com.example.hmi.audio.databinding.SongDataItemBinding
import com.example.hmi.audio.viewmodel.MediaViewModel

class ElementListAdapter(
        private val mediaViewModel: MediaViewModel
) : BaseAdapter() {

    var elementList: MutableList<Element>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount() = if (elementList != null) elementList!!.size else 0

    override fun getItem(position: Int) = elementList!![position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: SongDataItemBinding = if (view == null) {
            val inflater = LayoutInflater.from(viewGroup.context)
            SongDataItemBinding.inflate(inflater, viewGroup, false)
        } else {
            DataBindingUtil.getBinding(view)!!
        }

        binding.apply {
            this.element = elementList!![position]
            this.position = position
            this.listener = object: ElementSelectedListener {
                override fun onElementSelected(view: View, position: Int) {
                    mediaViewModel.elementSelected(view, position)
                }
            }
        }
        return binding.root
    }

    interface ElementSelectedListener {
        fun onElementSelected(view: View, position: Int)
    }
}
