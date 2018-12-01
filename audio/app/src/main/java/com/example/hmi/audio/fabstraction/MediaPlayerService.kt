package com.example.hmi.audio.fabstraction

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import com.example.hmi.audio.common.PlayingSongData
import com.example.hmi.audio.common.Track

import java.io.IOException

class MediaPlayerService : Service() {

    private val METADATA_UPDATE_INTERNVAL_MS = 300L

    private val mediaPlayer = android.media.MediaPlayer()

    private val handler: Handler

    private val clients: MutableList<AudioClient> = mutableListOf()

    private var periodicalUpdateStarted = false

    private val binder: IBinder = MediaPlayerBinder()

    inner class MediaPlayerBinder: Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    var track: Track? = null
        set(song) {
            field = song
            initializeMediaPlayer()
        }

    init {
        val handlerThread = HandlerThread("")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    private fun initializeMediaPlayer() {
        mediaPlayer.reset()
        try {
            mediaPlayer.setDataSource(
                track!!.fileDescriptor,
                track!!.aFileDescriptor.startOffset,
                track!!.aFileDescriptor.length
            )
            mediaPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun periodicalMetadataUpdate() {
        if (mediaPlayer.isPlaying) {
            onMetadataUpdate()
            handler.postDelayed({ periodicalMetadataUpdate() }, METADATA_UPDATE_INTERNVAL_MS)
        } else {
            periodicalUpdateStarted = false
        }
    }

    fun start() {
        mediaPlayer.start()
        if (!periodicalUpdateStarted) {
            periodicalMetadataUpdate()
            periodicalUpdateStarted = true
        }
    }

    fun pause() {
        mediaPlayer.pause()
        onMetadataUpdate()
    }

    fun seek(position: Int) {
        mediaPlayer.seekTo(position)
        onMetadataUpdate()
    }

    fun registerClient(client: AudioClient): Boolean {
        val result: Boolean = clients.add(client)
        onMetadataUpdate()
        return result
    }

    fun unregisterClient(client: AudioClient) = clients.remove(client)

    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mediaPlayer.setOnCompletionListener(listener)
    }

    private fun onMetadataUpdate() {
        if (track != null) {
            for (client in clients) {
                client.onMetadataUpdate(
                    PlayingSongData(
                        track!!,
                        mediaPlayer.currentPosition,
                        mediaPlayer.duration,
                        mediaPlayer.isPlaying
                    )
                )
            }
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: MediaPlayerService? = null

        fun getInstance(): MediaPlayerService {
            if (INSTANCE == null) {
                synchronized(MediaPlayerService::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MediaPlayerService()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
