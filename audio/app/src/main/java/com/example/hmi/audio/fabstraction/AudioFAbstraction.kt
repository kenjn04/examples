package com.example.hmi.audio.fabstraction

import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.common.PlayingSongData

class AudioFAbstraction private constructor(
    context: Context
): AudioClient {

    private lateinit var audioService: AudioService

    private var bound = false

    init {
        bindService(context)
    }

    private fun getServiceConnection(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val audioBinder = binder as AudioService.AudioBinder
                audioService = audioBinder.getService()
                bound = true
                onServerConnected()
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }
    }

    private fun bindService(context: Context) {
        val intent = Intent(context, AudioService::class.java)
        context.bindService(intent, getServiceConnection(), Context.BIND_AUTO_CREATE)
    }

    private fun onServerConnected() {
        audioService.registerClient(this)
    }

    val songData = MutableLiveData<PlayingSongData>()

    var song: Song?= null
        set(song) {
            audioService.song = song
        }

    fun play() = audioService.start()
    fun stop() = audioService.pause()
    fun seek(position: Int) = audioService.seek(position)

    override fun onMetadataUpdate(playingSongData: PlayingSongData) {
        songData.postValue(playingSongData)
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
    }
}
