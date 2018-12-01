package com.example.hmi.audio.usecase

import com.example.hmi.audio.repository.audio.AudioRepository
import io.reactivex.Completable

class RepeatModeIncrementTask(
        private val audioRepository: AudioRepository
) {

    fun execute(): Completable {
        return Completable.fromAction {
            audioRepository.incrementRepeatMode()
        }
    }
}
