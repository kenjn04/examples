package com.example.hmi.audio.common

import android.content.res.AssetFileDescriptor
import android.media.MediaMetadataRetriever
import java.io.FileDescriptor

class Track private constructor() : Element {

    override val type = Element.Type.TRACK

    override var title: String = "No Title"
    var artists       : String = "Unknown Artist"
    var genre         : String = "No Genre"
    var albumTitle    : String = "No AlbumList Title"
    var albumArtist   : String = "No AlbumList Artist"
    var trackNumber   : String = "0"
    var duration      : Long   = 0
    var albumArt      : ByteArray? = null

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
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)?.let {
            genre = it
        }
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)?.let {
            albumTitle = it
        }
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)?.let {
            albumArtist = it
        }
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)?.let {
            trackNumber = it
        }
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.let {
            duration = it.toLong()
        }
        retriever.embeddedPicture?.let {
            albumArt = it
        }
    }

    override fun toString(): String {
        return "Title: " + this.title +
                ", Artist: " + this.artists +
                ", Genre: " + this.genre +
                ", AlbumList Title: " + this.albumTitle +
                ", AlbumList Artist: " + this.albumArtist +
                ", Track Number: " + this.trackNumber
    }
}
