package com.example.hmi.audio.usecase

import com.example.hmi.audio.common.Song
import com.example.hmi.audio.fabstraction.AudioServiceFA
import io.reactivex.Completable

class SetSongTask(
        private val audioService: AudioServiceFA
) {

    fun setSong(songList: List<Song>, position: Int): Completable {
        return Completable.fromAction {
            audioService.songList = songList
            audioService.position = position
            audioService.play()
        }
    }
}
