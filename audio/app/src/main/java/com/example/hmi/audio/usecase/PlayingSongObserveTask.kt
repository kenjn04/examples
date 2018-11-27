package com.example.hmi.audio.usecase

import android.arch.lifecycle.MutableLiveData
import com.example.hmi.audio.common.PlayingSongData
import com.example.hmi.audio.fabstraction.AudioFAbstraction

import io.reactivex.Single

class PlayingSongObserveTask(
        private val audioService: AudioFAbstraction
) {

    fun execute(): Single<MutableLiveData<PlayingSongData>> {
        return Single.fromCallable { audioService.songData }
    }

}
