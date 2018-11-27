package com.example.hmi.audio.repository

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaMetadataRetriever
import com.example.hmi.audio.common.Song

import java.io.IOException
import kotlin.collections.ArrayList

class AssetDataProvider(context: Context) : MediaDataProvider {

    private var songList: ArrayList<Song>? = null

    init {
        songList = fetchSongList(context)
    }

    private fun fetchSongList(context: Context): ArrayList<Song> {
        val fetchingSongList = ArrayList<Song>()

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
                fetchingSongList.add(song)
            }
        }
        return fetchingSongList
    }

    override fun getSongList(): ArrayList<Song>? {
        return songList
    }

    companion object {

        @Volatile
        private var INSTANCE: AssetDataProvider? = null
        fun getInstance(context: Context): AssetDataProvider {
            if (INSTANCE == null) {
                synchronized(AssetDataProvider::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = AssetDataProvider(context)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
