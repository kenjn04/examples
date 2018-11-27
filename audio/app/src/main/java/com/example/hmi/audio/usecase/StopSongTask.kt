package com.example.hmi.audio.usecase

import com.example.hmi.audio.fabstraction.AudioFAbstraction
import io.reactivex.Completable

class StopSongTask(
        private val audioService: AudioFAbstraction
) {

    fun stopSong(): Completable {
        return Completable.fromAction {
            audioService.stop()
        }
    }
}
