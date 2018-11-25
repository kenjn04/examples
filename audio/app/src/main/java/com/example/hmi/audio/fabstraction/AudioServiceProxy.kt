package com.example.hmi.audio.fabstraction

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.service.IAudioClient
import com.example.hmi.audio.service.IAudioService

class AudioServiceProxy {

    private val PACKAGE_NAME: String = "com.example.hmi.audio"

    private lateinit var audioService: IAudioService

    private lateinit var serverConnectedCallback: ServerConnectedCallback

    private val mAudioConnection: ServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            audioService = IAudioService.Stub.asInterface(binder)
            serverConnectedCallback.onServerConnected()
        }
        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    fun bindService(context: Context, callback: ServerConnectedCallback) {
        serverConnectedCallback = callback

        val intent = Intent(IAudioService::class.java.name)
        intent.`package` = PACKAGE_NAME
        context.bindService(intent, mAudioConnection, Context.BIND_AUTO_CREATE)
    }

    var songList: List<Song> = listOf()
        set(list: List<Song>) {
            audioService.setSongList(list)
        }

    var position: Int = 0
        set(pos: Int) {
            audioService.setPosition(pos)
        }

    fun play()                               = audioService.start()
    fun pause()                              = audioService.pause()
    fun seek(position: Int)                  = audioService.seek(position)
    fun registerClient(client: IAudioClient) = audioService.registerClient(client)

    interface ServerConnectedCallback {
        fun onServerConnected()
    }
}
