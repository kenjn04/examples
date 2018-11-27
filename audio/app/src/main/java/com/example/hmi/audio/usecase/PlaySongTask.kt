package com.example.hmi.audio.usecase

import com.example.hmi.audio.fabstraction.AudioFAbstraction
import io.reactivex.Completable

class PlaySongTask(
        private val audioService: AudioFAbstraction
) {

    fun playSong(): Completable {
        return Completable.fromAction { audioService.play() }
    }
}
