package com.example.hmi.audio.repository

import com.example.hmi.audio.common.Song

class USBDataProvider : MediaDataProvider {

    override fun getSongList(): ArrayList<Song>? {
        return null
    }

    companion object {

        @Volatile
        private var INSTANCE: USBDataProvider? = null
        fun getInstance(): USBDataProvider {
            if (INSTANCE == null) {
                synchronized(USBDataProvider::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = USBDataProvider()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}

