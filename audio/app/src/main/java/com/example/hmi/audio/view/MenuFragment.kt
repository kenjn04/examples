package com.example.hmi.audio.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hmi.audio.viewmodel.MediaViewModel

import com.example.hmi.audio.databinding.FragmentMenuBinding
import com.example.hmi.audio.view.adapter.SongGroupAdapter
import org.koin.android.viewmodel.ext.android.sharedViewModel

class MenuFragment : Fragment() {

    private lateinit var fragmentMenuBinding: FragmentMenuBinding

    private val mediaViewModel by sharedViewModel<MediaViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentMenuBinding = FragmentMenuBinding.inflate(inflater, container, false)
        return fragmentMenuBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fragmentMenuBinding.apply {
            this.viewModel = mediaViewModel
            this.songGroup.adapter = SongGroupAdapter(mediaViewModel)
        }
    }
}