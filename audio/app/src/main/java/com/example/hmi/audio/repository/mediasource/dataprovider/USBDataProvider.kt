package com.example.hmi.audio.repository.mediasource.dataprovider

import com.example.hmi.audio.common.Song

class USBDataProvider private constructor(): MediaDataProvider {

    override var songList: ArrayList<Song>? = null

    companion object {

        @Volatile
        private var INSTANCE: USBDataProvider? = null
        fun getInstance(): USBDataProvider {
            if (INSTANCE == null) {
                synchronized(USBDataProvider::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE =
                                USBDataProvider()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}

