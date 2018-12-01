package com.example.hmi.audio.common

open class TrackList(override val title: String) : _TrackList(mutableListOf()) {

    override val type = Element.Type.TRACK_LIST

    fun add(track: Track): Boolean = add(track, true)

    override fun add(element: Element): Boolean = add(element, false)

    fun add(element: Element, allowed: Boolean): Boolean {
        return if (allowed) {
            super.add(element)
        } else {
            false
        }
    }
}

abstract class _TrackList(val trackList: MutableList<Element>) : Element, MutableList<Element> by trackList

