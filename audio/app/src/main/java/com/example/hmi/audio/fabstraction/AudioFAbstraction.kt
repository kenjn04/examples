package com.example.hmi.audio.fabstraction

import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.MediaStore
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.common.PlayingSongData

class AudioFAbstraction private constructor(
    context: Context
): AudioClient {

    private lateinit var mediaPlayerService: MediaPlayerService

    private var bound = false

    val playingSongData = MutableLiveData<PlayingSongData>()

    var song: Song?
        set(song) {
            mediaPlayerService.song = song
        }
        get() {
            return if (bound) {
                mediaPlayerService.song
            } else
                null
        }

    private var pendingOnCompletionLister: MediaPlayer.OnCompletionListener? = null

    fun play() = mediaPlayerService.start()
    fun stop() = mediaPlayerService.pause()
    fun seek(position: Int) = mediaPlayerService.seek(position)

    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        if (bound) {
            mediaPlayerService.setOnCompletionListener(listener)
        } else {
            pendingOnCompletionLister = listener
        }
    }

    init {
        bindService(context)
    }

    override fun onMetadataUpdate(playingSongData: PlayingSongData) {
        this.playingSongData.postValue(playingSongData)
    }

    private fun getServiceConnection(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val audioBinder = binder as MediaPlayerService.MediaPlayerBinder
                mediaPlayerService = audioBinder.getService()
                bound = true
                onServerConnected()
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }
    }

    private fun bindService(context: Context) {
        val intent = Intent(context, MediaPlayerService::class.java)
        context.bindService(intent, getServiceConnection(), Context.BIND_AUTO_CREATE)
    }

    private fun onServerConnected() {
        mediaPlayerService.registerClient(this)
        if (pendingOnCompletionLister != null) {
            mediaPlayerService.setOnCompletionListener(pendingOnCompletionLister!!)
            pendingOnCompletionLister = null
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: AudioFAbstraction? = null

        fun getInstance(context: Context): AudioFAbstraction {
            if (INSTANCE == null) {
                synchronized(AudioFAbstraction::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = AudioFAbstraction(context)
                    }
                }
            }
            return INSTANCE!!
        }

        fun getInstance() = INSTANCE
    }
}
