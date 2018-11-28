package com.example.hmi.audio.repository.mediasource

import com.example.hmi.audio.common.MediaSource
import com.example.hmi.audio.common.Song

interface MediaSourceRepository{

    val songList: ArrayList<Song>?

    fun switchDataProvider(source: MediaSource)

}
