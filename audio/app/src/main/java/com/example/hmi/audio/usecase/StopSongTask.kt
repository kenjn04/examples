package com.example.hmi.audio.usecase

import com.example.hmi.audio.fabstraction.AudioServiceFA
import io.reactivex.Completable

class StopSongTask(
        private val audioService: AudioServiceFA
) {

    fun stopSong(): Completable {
        return Completable.fromAction {
            audioService.stop()
        }
    }
}
