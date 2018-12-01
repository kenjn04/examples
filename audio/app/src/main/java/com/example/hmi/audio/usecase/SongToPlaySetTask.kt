package com.example.hmi.audio.usecase

import com.example.hmi.audio.common.Element
import com.example.hmi.audio.common.Track
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import com.example.hmi.audio.repository.audio.AudioRepository
import io.reactivex.Completable
import java.lang.annotation.ElementType

class SongToPlaySetTask(
    private val audioFAbstraction: AudioFAbstraction,
    private val audioRepository: AudioRepository
) {

    fun execute(track: Track, sourceElementType: Element.Type): Completable {
        return Completable.fromAction {
            audioRepository.activeType = sourceElementType
            audioFAbstraction.track = track
            audioFAbstraction.play()
        }
    }
}
