package com.example.hmi.audio.repository.mediasource.dataprovider

import android.content.Context
import android.content.res.AssetFileDescriptor
import com.example.hmi.audio.common.*

import java.io.IOException

class AssetDataProvider private constructor(context: Context) : MediaDataProvider() {

    override var songList = SongList("Whole Songs")
    override var albums   = SongGroup(LibraryType.ALBUMS)
    override var artists  = SongGroup(LibraryType.ARTISTS)
    override var genres   = SongGroup(LibraryType.GENRES)

    init {
        createSongList(context)
        createListsOtherThanWholeSongs()
        sortEachList()
    }

    private fun createSongList(context: Context) {

        var fileList: Array<String>? = null
        try {
            fileList = context.assets.list("")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        for (file in fileList!!) {
            if (file.endsWith(".mp3")) {
                var afd: AssetFileDescriptor? = null
                try {
                    afd = context.assets.openFd(file)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val song = Song(file, afd!!)
                songList.add(song)
            }
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: AssetDataProvider? = null
        fun getInstance(context: Context): AssetDataProvider {
            if (INSTANCE == null) {
                synchronized(AssetDataProvider::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE =
                                AssetDataProvider(context)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
