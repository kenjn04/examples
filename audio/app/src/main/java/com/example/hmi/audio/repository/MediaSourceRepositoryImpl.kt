package com.example.hmi.audio.repository

import com.example.hmi.audio.common.MediaSource
import com.example.hmi.audio.common.Song

class MediaSourceRepositoryImpl(
        private val assetDataProvider: AssetDataProvider,
        private val usbDataProvider: USBDataProvider
) : MediaSourceRepository {

    private var currentDataProvider: MediaDataProvider

    init {
        currentDataProvider = assetDataProvider
    }

    override fun switchDataProvider(source: MediaSource) {
        when (source) {
            MediaSource.ASSETS -> {
                currentDataProvider = assetDataProvider
            }
            MediaSource.USB -> {
                currentDataProvider = usbDataProvider
            }
        }
    }

    override fun getSongList(): ArrayList<Song>? = currentDataProvider.getSongList()

    companion object {

        @Volatile
        private var INSTANCE: MediaSourceRepositoryImpl? = null
        fun getInstance(
                assetDataProvider: AssetDataProvider,
                usbDataProvider: USBDataProvider
        ): MediaSourceRepository {
            if (INSTANCE == null) {
                synchronized(MediaSourceRepositoryImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MediaSourceRepositoryImpl(
                                assetDataProvider,
                                usbDataProvider
                        )
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
