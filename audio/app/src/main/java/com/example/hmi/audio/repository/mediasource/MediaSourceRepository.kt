package com.example.hmi.audio.repository.mediasource

import com.example.hmi.audio.common.Element
import com.example.hmi.audio.common.MediaSource
import com.example.hmi.audio.common.TrackList
import com.example.hmi.audio.common.TrackListGroup

interface MediaSourceRepository{

    fun getSpecifiedList(type: Element.Type): TrackList?

    fun switchDataProvider(source: MediaSource)

}
