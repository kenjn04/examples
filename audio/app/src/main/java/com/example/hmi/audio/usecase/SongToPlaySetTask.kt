package com.example.hmi.audio.usecase

import com.example.hmi.audio.common.Song
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import io.reactivex.Completable

class SongToPlaySetTask(
        private val audioFAbstraction: AudioFAbstraction
) {

    fun execute(song: Song): Completable {
        return Completable.fromAction {
            audioFAbstraction.song = song
            audioFAbstraction.play()
        }
    }
}
