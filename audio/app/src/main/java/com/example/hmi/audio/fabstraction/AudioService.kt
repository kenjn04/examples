package com.example.hmi.audio.fabstraction

import android.app.Service
import android.content.Intent
import android.os.*
import com.example.hmi.audio.common.Song

import java.io.IOException

class AudioService : Service() {

    private val mediaPlayer = android.media.MediaPlayer()

    private val handler: Handler

    private lateinit var songList: List<Song>

    private var position: Int = 0

    private val clients: MutableList<AudioClient> = mutableListOf()

    private var periodicalUpdateStarted = false

    private val binder: IBinder = AudioBinder()

    inner class AudioBinder: Binder() {
        fun getService(): AudioService = this@AudioService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    init {
        mediaPlayer.setOnCompletionListener {
            position++
            position %= songList.size
            initializeMediaPlayer()
            start()
        }
        val handlerThread = HandlerThread("")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    private fun initializeMediaPlayer() {
        if (this::songList.isInitialized) {
            val song: Song = songList[position]
            mediaPlayer.reset()
            try {
                mediaPlayer.setDataSource(song.fileDescriptor)
                mediaPlayer.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun periodicalMetadataUpdate() {
        if (mediaPlayer.isPlaying) {
            onMetadataUpdate()
            handler.postDelayed({ periodicalMetadataUpdate() }, 500)
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

    fun setSongList(list: List<Song>) {
        songList = list
        initializeMediaPlayer()
        onMetadataUpdate()
    }

    fun setPosition(pos: Int) {
        position = pos
        initializeMediaPlayer()
        onMetadataUpdate()
    }

    fun registerClient(client: AudioClient): Boolean {
        val result: Boolean = clients.add(client)
        onMetadataUpdate()
        return result
    }

    fun unregisterClient(client: AudioClient) = clients.remove(client)

    private fun onMetadataUpdate() {
        if (this::songList.isInitialized) {
            for (client in clients) {
                client.onMetadataUpdate(
                    songList[position].title!!,
                    mediaPlayer.currentPosition,
                    mediaPlayer.duration,
                    mediaPlayer.isPlaying
                )
            }
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: AudioService? = null

        fun getInstance(): AudioService {
            if (INSTANCE == null) {
                synchronized(AudioService::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = AudioService()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
