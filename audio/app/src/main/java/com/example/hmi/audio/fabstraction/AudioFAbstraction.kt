package com.example.hmi.audio.fabstraction

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.common.SongData

class AudioFAbstraction(
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

    val songData = MutableLiveData<SongData>()

    var songList: List<Song> = listOf()
        set(list: List<Song>) {
            audioService.setSongList(list)
        }

    var position: Int = 0
        set(pos: Int) {
            audioService.setPosition(pos)
        }

    fun play() = audioService.start()
    fun stop() = audioService.pause()
    fun seek(position: Int) = audioService.seek(position)

    fun setSongDataUpdateObserver(observer: Observer<SongData>) {
        songData.observeForever(observer)
    }

    override fun onMetadataUpdate(track: String, progress: Int, duration: Int, isPlaying: Boolean) {
        songData.postValue(SongData(track, progress, duration, isPlaying))
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
