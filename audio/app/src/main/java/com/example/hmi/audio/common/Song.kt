package com.example.hmi.audio.common

import android.content.res.AssetFileDescriptor
import android.media.MediaMetadataRetriever
import java.io.FileDescriptor

class Song private constructor() {
    var albumTitle: String? = null
    var albumArtists: String? = null
    var artists: String? = null
    var trackNumber: String? = null
    var duration: Long? = null
    var genre: String? = null
    var title: String? = null
    var albumArt: ByteArray? = null

    lateinit var fileDescriptor: FileDescriptor
    lateinit var aFileDescriptor: AssetFileDescriptor

    constructor(assetFileDescriptor: AssetFileDescriptor) : this() {
        fileDescriptor = assetFileDescriptor.fileDescriptor
        aFileDescriptor = assetFileDescriptor
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(fileDescriptor)
        retrieveMetadata(retriever)
    }

    constructor(track: String, fileDescriptor: AssetFileDescriptor): this(fileDescriptor) {
        title = track
    }

    private fun retrieveMetadata(retriever: MediaMetadataRetriever) {
        albumTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        albumArtists = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
        artists = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        trackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
        duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLongOrNull()
        genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
        title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        albumArt = retriever.embeddedPicture
    }
}
