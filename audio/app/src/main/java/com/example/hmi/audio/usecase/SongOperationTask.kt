package com.example.hmi.audio.usecase

import com.example.hmi.audio.common.MediaOperation
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import io.reactivex.Completable

class SongOperationTask(
        private val audioFAbstraction: AudioFAbstraction
) {

    fun execute(operation: MediaOperation, seekPosition: Int): Completable {
        return Completable.fromAction {
            when (operation) {
                MediaOperation.PLAY -> {
                    audioFAbstraction.play()
                }
                MediaOperation.STOP -> {
                    audioFAbstraction.stop()
                }
                MediaOperation.SEEK -> {
                    audioFAbstraction.seek(seekPosition)
                }

            }
        }
    }
}
