package com.example.hmi.audio.repository.mediasource

import com.example.hmi.audio.common.LibraryType
import com.example.hmi.audio.common.SongGroupEntry
import com.example.hmi.audio.common.MediaSource
import com.example.hmi.audio.common.SongList
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

    override fun getSpecifiedList(type: LibraryType): SongList? {
        return when (type) {
            LibraryType.SONGS   -> dataProvider.songList
            LibraryType.ALBUMS  -> dataProvider.albums
            LibraryType.ARTISTS -> dataProvider.artists
            LibraryType.GENRES  -> dataProvider.genres
        }
    }

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

        fun getInstance() = INSTANCE
    }
}
