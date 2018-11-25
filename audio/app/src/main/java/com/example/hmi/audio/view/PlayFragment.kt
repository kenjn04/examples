package com.example.hmi.audio.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hmi.audio.viewmodel.MediaViewModel

import com.example.hmi.audio.databinding.FragmentPlayBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PlayFragment : Fragment() {

    lateinit var fragmentPlayBinding: FragmentPlayBinding

    private val mediaViewModel by sharedViewModel<MediaViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentPlayBinding = FragmentPlayBinding.inflate(inflater, container, false)
        return fragmentPlayBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fragmentPlayBinding.apply {
            this.viewModel = mediaViewModel
        }
    }
}