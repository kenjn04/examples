package com.example.hmi.audio.repository.audio

import android.arch.lifecycle.MutableLiveData
import com.example.hmi.audio.common.RepeatMode

interface AudioRepository{

    val repeatMode: MutableLiveData<RepeatMode>

    fun incrementRepeatMode()
}
