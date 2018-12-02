package com.example.hmi.audio.common

/**
 * List of Song list. This has several SongLists.
 */
class SongGroup(override val type: LibraryType) : SongList("") {

    fun add(songList: SongList): Boolean = super.add(songList, true)
}

