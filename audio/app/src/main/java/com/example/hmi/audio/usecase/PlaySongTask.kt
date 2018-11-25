package com.example.hmi.audio.usecase

import com.example.hmi.audio.fabstraction.AudioServiceFA
import io.reactivex.Completable

class PlaySongTask(
        private val audioService: AudioServiceFA
) {

    fun playSong(): Completable {
        return Completable.fromAction { audioService.play() }
    }
}
