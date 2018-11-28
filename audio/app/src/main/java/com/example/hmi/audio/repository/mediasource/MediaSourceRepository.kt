package com.example.hmi.audio.repository.mediasource

import com.example.hmi.audio.common.MediaSource
import com.example.hmi.audio.common.Song

interface MediaSourceRepository{

    val songList: MutableList<Song>?

    fun switchDataProvider(source: MediaSource)

}
