package com.example.hmi.audio.usecase

import android.provider.MediaStore
import com.example.hmi.audio.common.MediaOperation
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import io.reactivex.Completable

class SongOperationTask(
        private val audioService: AudioFAbstraction
) {

    fun execute(operation: MediaOperation, seekPosition: Int = -1): Completable {
        return Completable.fromAction {
            when (operation) {
                MediaOperation.PLAY -> {
                    audioService.play()
                }
                MediaOperation.STOP -> {
                    audioService.stop()
                }
                MediaOperation.SEEK -> {
                    audioService.seek(seekPosition)
                }

            }
            audioService.play()
        }
    }
}
