package com.example.hmi.audio.view.adapter

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.databinding.SongDataItemBinding
import com.example.hmi.audio.viewmodel.MediaViewModel

class SongListAdapter(
        private val mediaViewModel: MediaViewModel
) : BaseAdapter() {

    var songList: List<Song>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount() = if (songList != null) songList!!.size else 0

    override fun getItem(position: Int) = songList!![position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: SongDataItemBinding = if (view == null) {
            val inflater = LayoutInflater.from(viewGroup.context)
            SongDataItemBinding.inflate(inflater, viewGroup, false)
        } else {
            DataBindingUtil.getBinding(view)!!
        }

        binding.apply {
            this.song = songList!![position]
            this.position = position
            this.listener = object: SongSelectedListener {
                override fun onSongSelected(view: View, position: Int) {
                    mediaViewModel.songSelected(view, position)
                }
            }
        }
        return binding.root
    }

    interface SongSelectedListener {
        fun onSongSelected(view: View, position: Int)
    }
}
