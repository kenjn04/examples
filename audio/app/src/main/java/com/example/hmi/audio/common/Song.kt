package com.example.hmi.audio.common

import android.content.res.AssetFileDescriptor
import android.media.MediaMetadataRetriever
import java.io.FileDescriptor

class Song private constructor() {

    var title      : String = "No Title"
    var artists    : String = "Unknown Artist"
    var albumTitle : String = "No Album Title"
    var genre      : String = "No Genre"
    var duration   : Long   = 0
    var albumArt   : ByteArray? = null

    lateinit var fileDescriptor: FileDescriptor
    lateinit var aFileDescriptor: AssetFileDescriptor

    constructor(assetFileDescriptor: AssetFileDescriptor) : this() {
        fileDescriptor = assetFileDescriptor.fileDescriptor
        aFileDescriptor = assetFileDescriptor
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(
            fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )
        retrieveMetadata(retriever)
    }

    constructor(track: String, fileDescriptor: AssetFileDescriptor): this(fileDescriptor) {
        if (title == "No Title") {
            title = track
        }
    }

    private fun retrieveMetadata(retriever: MediaMetadataRetriever) {
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)?.let {
            title = it
        }
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)?.let {
            artists = it
        }
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)?.let {
            albumTitle = it
        }
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)?.let {
            genre = it
        }
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.let {
            duration = it.toLong()
        }
        retriever.embeddedPicture?.let {
            albumArt = it
        }
    }
}
