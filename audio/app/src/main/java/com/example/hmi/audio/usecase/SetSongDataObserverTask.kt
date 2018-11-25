package com.example.hmi.audio.usecase

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.example.hmi.audio.common.SongData
import com.example.hmi.audio.fabstraction.AudioServiceFA

import io.reactivex.Completable
import io.reactivex.Single

class SetSongDataObserverTask(
        private val audioService: AudioServiceFA
) {

    fun getSongData(): Single<MutableLiveData<SongData>> {
        return Single.fromCallable { audioService.songData }
    }

    fun setSongDataUpdateObserver(observer: Observer<SongData>): Completable {
        return Completable.fromAction { audioService.setSongDataUpdateObserver(observer) }
    }
}
