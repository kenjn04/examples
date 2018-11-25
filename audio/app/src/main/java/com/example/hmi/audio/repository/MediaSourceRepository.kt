package com.example.hmi.audio.repository

import com.example.hmi.audio.common.MediaSource
import com.example.hmi.audio.common.Song

interface MediaSourceRepository{

    fun switchDataProvider(source: MediaSource)

    fun getSongList(): ArrayList<Song>?
}
