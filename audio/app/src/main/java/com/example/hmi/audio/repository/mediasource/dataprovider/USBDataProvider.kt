package com.example.hmi.audio.repository.mediasource.dataprovider

import com.example.hmi.audio.common.*

class USBDataProvider private constructor(): MediaDataProvider() {

    override var songList = SongList("Whole Songs")
    override var albums   = SongGroup(LibraryType.ALBUMS)
    override var artists  = SongGroup(LibraryType.ARTISTS)
    override var genres   = SongGroup(LibraryType.GENRES)

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

