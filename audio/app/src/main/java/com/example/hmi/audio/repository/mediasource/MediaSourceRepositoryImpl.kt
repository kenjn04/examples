package com.example.hmi.audio.repository.mediasource

import com.example.hmi.audio.common.MediaSource
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.repository.mediasource.dataprovider.AssetDataProvider
import com.example.hmi.audio.repository.mediasource.dataprovider.MediaDataProvider
import com.example.hmi.audio.repository.mediasource.dataprovider.USBDataProvider

class MediaSourceRepositoryImpl private constructor(
    private val assetDataProvider: AssetDataProvider,
    private val usbDataProvider: USBDataProvider
) : MediaSourceRepository {

    private var dataProvider: MediaDataProvider

    init {
        dataProvider = assetDataProvider
    }

    override val songList: MutableList<Song>? = dataProvider.songList

    override fun switchDataProvider(source: MediaSource) {
        dataProvider =
                when (source) {
                    MediaSource.ASSETS -> {
                        assetDataProvider
                    }
                    MediaSource.USB -> {
                        usbDataProvider
                    }
                }
    }

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
                        INSTANCE =
                                MediaSourceRepositoryImpl(
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
