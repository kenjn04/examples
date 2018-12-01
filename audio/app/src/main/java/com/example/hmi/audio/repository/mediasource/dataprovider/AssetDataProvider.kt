package com.example.hmi.audio.repository.mediasource.dataprovider

import android.content.Context
import android.content.res.AssetFileDescriptor
import com.example.hmi.audio.common.*

import java.io.IOException

class AssetDataProvider private constructor(context: Context) : MediaDataProvider() {

    override var trackList   = TrackList("whole track")
    override var albumList   = TrackListGroup(Element.Type.ALBUM)
    override var artistsList = TrackListGroup(Element.Type.ARTISTS)
    override var genreList   = TrackListGroup(Element.Type.GENRE)

    init {
        createTrackList(context)
        createListsOtherThanWholeTrack()
        sortEachList()
    }

    private fun createTrackList(context: Context) {

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
                val track = Track(file, afd!!)
                trackList.add(track)
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
