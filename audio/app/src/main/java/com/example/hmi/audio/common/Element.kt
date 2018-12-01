package com.example.hmi.audio.common

interface Element {

    val type: Type

    val title: String

    enum class Type {

        TRACK,

        TRACK_LIST,

        ALBUM,

        ARTISTS,

        GENRE
    }
}
