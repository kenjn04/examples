package com.example.hmi.audio.common

class TrackListGroup(override val type: Element.Type) : TrackList("") {

    fun add(trackList: TrackList): Boolean = super.add(trackList, true)
}

