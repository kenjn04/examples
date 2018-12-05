package com.example.hmi.audio.view

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.hmi.audio.AudioApplication

import com.example.hmi.audio.BaseActivity
import com.example.hmi.audio.R

class MediaActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
    }

    override fun onStart() {
        super.onStart()
        (application as AudioApplication).requestUpdateWidget()
    }
}
