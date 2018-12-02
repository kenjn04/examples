package com.example.hmi.audio.repository.audio

import android.arch.lifecycle.MutableLiveData
import com.example.hmi.audio.common.LibraryType
import com.example.hmi.audio.common.SongGroupEntry
import com.example.hmi.audio.common.RepeatMode

interface AudioRepository{

    val repeatMode: MutableLiveData<RepeatMode>

    var activeType: LibraryType

    fun incrementRepeatMode()
}
