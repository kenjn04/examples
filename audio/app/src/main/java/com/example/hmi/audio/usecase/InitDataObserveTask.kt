package com.example.hmi.audio.usecase

import android.arch.lifecycle.MutableLiveData
import android.media.MediaPlayer
import com.example.hmi.audio.common.PlayingSongData
import com.example.hmi.audio.common.RepeatMode
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import com.example.hmi.audio.repository.audio.AudioRepository
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository
import com.example.hmi.audio.usecase.listener.MediaPlayerCompletionListener

import io.reactivex.Single

class InitDataObserveTask(
        private val audioFAbstraction: AudioFAbstraction,
        private val audioRepository: AudioRepository,
        private val mediaSourceRepository: MediaSourceRepository
) {
    fun execute(): Single<OutputData> {
        return Single.fromCallable {
            audioFAbstraction.setOnCompletionListener(
                MediaPlayerCompletionListener(audioFAbstraction, audioRepository, mediaSourceRepository)
            )
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
}
