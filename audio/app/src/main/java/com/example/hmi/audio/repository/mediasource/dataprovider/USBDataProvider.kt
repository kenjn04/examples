package com.example.hmi.audio.repository.mediasource.dataprovider

import com.example.hmi.audio.common.*

class USBDataProvider private constructor(): MediaDataProvider() {

    override var trackList   = TrackList("whole track")
    override var albumList   = TrackListGroup(Element.Type.ALBUM)
    override var artistsList = TrackListGroup(Element.Type.ARTISTS)
    override var genreList   = TrackListGroup(Element.Type.GENRE)

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

