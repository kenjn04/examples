package com.example.hmi.audio.common

/**
 * List of Songs. This has several songs.
 */
open class SongList(override val title: String) : ASongList(mutableListOf()) {

    override val type = LibraryType.SONGS

    constructor(source: SongList) : this(source.title) {
        if (source.type == LibraryType.SONGS) {
            for (song in source) {
                add(song as Song)
            }
        }
    }

    fun add(song: Song): Boolean = add(song, true)

    override fun add(element: SongGroupEntry): Boolean = add(element, false)

    fun add(entry: SongGroupEntry, allowed: Boolean): Boolean {
        return if (allowed) {
            super.add(entry)
        } else {
            false
        }
    }
}

abstract class ASongList(val songList: MutableList<SongGroupEntry>) : SongGroupEntry, MutableList<SongGroupEntry> by songList {
    abstract val type: LibraryType
}


