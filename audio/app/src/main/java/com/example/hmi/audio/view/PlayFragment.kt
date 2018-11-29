package com.example.hmi.audio.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.hmi.audio.viewmodel.MediaViewModel

import com.example.hmi.audio.databinding.FragmentPlayBinding
import kotlinx.android.synthetic.main.fragment_play.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PlayFragment : Fragment() {

    private lateinit var fragmentPlayBinding: FragmentPlayBinding

    private val mediaViewModel by sharedViewModel<MediaViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentPlayBinding = FragmentPlayBinding.inflate(inflater, container, false)
        return fragmentPlayBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setup()
        fragmentPlayBinding.apply {
            this.viewModel = mediaViewModel
        }
    }

    private fun setup() {

        previous_song_button.apply {
            setOnTouchListener(SongSpeedChangeListener(-4))
        }
        next_song_button.apply {
            setOnTouchListener(SongSpeedChangeListener(4))
        }
    }

    inner class SongSpeedChangeListener(private val speed: Int) : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    mediaViewModel.startSongSpeedChange(speed)
                }
                MotionEvent.ACTION_UP -> {
                    return mediaViewModel.cancelSongSpeedChange()
                }
            }
            return false
        }

    }
}