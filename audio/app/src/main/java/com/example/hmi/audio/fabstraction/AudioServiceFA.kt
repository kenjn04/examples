package com.example.hmi.audio.fabstraction

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.util.Log
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.common.SongData
import com.example.hmi.audio.service.IAudioClient

class AudioServiceFA(
        context: Context,
        private val audioServiceProxy: AudioServiceProxy
): IAudioClient.Stub(), AudioServiceProxy.ServerConnectedCallback {

    init {
        audioServiceProxy.bindService(context, this)
    }

    override fun onServerConnected() {
        audioServiceProxy.registerClient(this)
    }

    val songData = MutableLiveData<SongData>()

    var songList: List<Song> = listOf()
        set(list: List<Song>) {
            audioServiceProxy.songList = list
        }

    var position: Int = 0
        set(pos: Int) {
            audioServiceProxy.position = pos
        }

    fun play() = audioServiceProxy.play()
    fun stop() = audioServiceProxy.pause()
    fun seek(position: Int) = audioServiceProxy.seek(position)

    fun setSongDataUpdateObserver(observer: Observer<SongData>) {
        songData.observeForever(observer)
    }

    override fun onMetadataUpdate(track: String?, progress: Int, duration: Int, isPlaying: Boolean) {
        songData.postValue(SongData(track!!, progress, duration, isPlaying))
    }

    companion object {

        @Volatile
        private var INSTANCE: AudioServiceFA? = null

        fun getInstance(context: Context, proxy: AudioServiceProxy): AudioServiceFA {
            if (INSTANCE == null) {
                synchronized(AudioServiceFA::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = AudioServiceFA(context, proxy)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
