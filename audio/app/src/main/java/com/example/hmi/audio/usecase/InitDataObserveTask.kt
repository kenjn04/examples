package com.example.hmi.audio.usecase

import android.arch.lifecycle.MutableLiveData
import android.media.MediaPlayer
import android.util.Log
import com.example.hmi.audio.common.PlayingSongData
import com.example.hmi.audio.common.RepeatMode
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import com.example.hmi.audio.repository.audio.AudioRepository

import io.reactivex.Single

class InitDataObserveTask(
        private val audioFAbstraction: AudioFAbstraction,
        private val audioRepository: AudioRepository
) {
    fun execute(): Single<OutputData> {
        return Single.fromCallable {
            audioFAbstraction.setOnCompletionListener(MediaPlayerCompletionLister())
            OutputData(
                audioFAbstraction.playingSongData,
                audioRepository.repeatMode
            )
        }
    }

    data class OutputData (
        val playingSongData: MutableLiveData<PlayingSongData>,
        val repeatMode: MutableLiveData<RepeatMode>
    )

    inner class MediaPlayerCompletionLister: MediaPlayer.OnCompletionListener {

        override fun onCompletion(mp: MediaPlayer?) {
            val song = audioFAbstraction.song!!
            val repeatMode = audioRepository.repeatMode.value!!

            Log.d("aaaaaa", song.title + " " + repeatMode.toString())

        }

    }

}
