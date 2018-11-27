package com.example.hmi.audio.usecase

import com.example.hmi.audio.common.Song
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import io.reactivex.Completable

class SongToPlaySetTask(
        private val audioService: AudioFAbstraction
) {

    fun execute(song: Song): Completable {
        return Completable.fromAction {
            audioService.song = song
            audioService.play()
        }
    }
}
