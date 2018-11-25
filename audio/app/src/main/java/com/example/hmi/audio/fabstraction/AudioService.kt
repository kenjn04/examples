package com.example.hmi.audio.fabstraction

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.RemoteCallbackList
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.service.IAudioClient
import com.example.hmi.audio.service.IAudioService

import java.io.IOException

class AudioService : Service() {

    private val mediaPlayer = android.media.MediaPlayer()

    private val handler: Handler

    private lateinit var songList: List<Song>

    private var position: Int = 0

    private val clients: RemoteCallbackList<IAudioClient> = RemoteCallbackList()

    private var periodicalUpdateStarted = false

    private val binder: IAudioService.Stub = object: IAudioService.Stub() {
        override fun start() = this@AudioService.start()
        override fun pause() = this@AudioService.pause()
        override fun seek(position: Int) = this@AudioService.seek(position)
        override fun setPosition(position: Int) = this@AudioService.setPosition(position)
        override fun setSongList(songList: List<Song>?) = this@AudioService.setSongList(songList!!)
        override fun registerClient(client: IAudioClient?) = this@AudioService.registerClient(client)
        override fun unregisterClient(client: IAudioClient?) = this@AudioService.unregisterClient(client)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return if (IAudioService::class.java.name.equals(intent?.action)) binder else null
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
                mediaPlayer.setDataSource(
                        song.fileDescriptor.fileDescriptor,
                        song.fileDescriptor.startOffset,
                        song.fileDescriptor.length)
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

    private fun start() {
        mediaPlayer.start()
        if (!periodicalUpdateStarted) {
            periodicalMetadataUpdate()
            periodicalUpdateStarted = true
        }
    }

    private fun pause() {
        mediaPlayer.pause()
        onMetadataUpdate()
    }

    private fun seek(position: Int) {
        mediaPlayer.seekTo(position)
        onMetadataUpdate()
    }

    private fun setSongList(list: List<Song>) {
        songList = list
        initializeMediaPlayer()
        onMetadataUpdate()
    }

    private fun setPosition(pos: Int) {
        position = pos
        initializeMediaPlayer()
        onMetadataUpdate()
    }

    private fun registerClient(client: IAudioClient?): Boolean {
        val result: Boolean = clients.register(client)
        onMetadataUpdate()
        return result
    }

    private fun unregisterClient(client: IAudioClient?) = clients.unregister(client)

    private fun onMetadataUpdate() {
        if (this::songList.isInitialized) {
            var clientNum = clients.beginBroadcast()
            --clientNum
            for (i in 0..clientNum) {
                clients.getBroadcastItem(i).onMetadataUpdate(
                        songList[position].track,
                        mediaPlayer.currentPosition,
                        mediaPlayer.duration,
                        mediaPlayer.isPlaying
                )
            }
            clients.finishBroadcast()
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: AudioService? = null

        fun getInstance(): AudioService? {
            if (INSTANCE == null) {
                synchronized(AudioService::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = AudioService()
                    }
                }
            }
            return INSTANCE
        }
    }
}
