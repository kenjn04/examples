package com.example.hmi.audio.repository.mediasource

import com.example.hmi.audio.common.LibraryType
import com.example.hmi.audio.common.SongGroupEntry
import com.example.hmi.audio.common.MediaSource
import com.example.hmi.audio.common.SongList

interface MediaSourceRepository{

    fun getSpecifiedList(type: LibraryType): SongList?

    fun switchDataProvider(source: MediaSource)

}
