package com.example.hmi.audio.repository

import com.example.hmi.audio.common.Song

interface MediaDataProvider {

    fun getSongList(): ArrayList<Song>?
}
