package com.example.hmi.audio.view.adapter

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.hmi.audio.common.SongGroupEntry
import com.example.hmi.audio.databinding.SongDataItemBinding
import com.example.hmi.audio.viewmodel.MediaViewModel

class SongGroupAdapter(
        private val mediaViewModel: MediaViewModel
) : BaseAdapter() {

    var songGroupEntryList: MutableList<SongGroupEntry>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount() = if (songGroupEntryList != null) songGroupEntryList!!.size else 0

    override fun getItem(position: Int) = songGroupEntryList!![position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: SongDataItemBinding = if (view == null) {
            val inflater = LayoutInflater.from(viewGroup.context)
            SongDataItemBinding.inflate(inflater, viewGroup, false)
        } else {
            DataBindingUtil.getBinding(view)!!
        }

        binding.apply {
            this.entry = songGroupEntryList!![position]
            this.position = position
            this.listener = object: OnSongGroupEntrySelectedListener {
                override fun onSongGroupEntrySelected(view: View, position: Int) {
                    mediaViewModel.songGroupEntrySelected(view, position)
                }
            }
        }
        return binding.root
    }

    interface OnSongGroupEntrySelectedListener {
        fun onSongGroupEntrySelected(view: View, position: Int)
    }
}
