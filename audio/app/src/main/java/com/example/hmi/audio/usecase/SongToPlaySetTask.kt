package com.example.hmi.audio.usecase

import com.example.hmi.audio.common.LibraryType
import com.example.hmi.audio.common.SongGroupEntry
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import com.example.hmi.audio.repository.audio.AudioRepository
import io.reactivex.Completable

class SongToPlaySetTask(
    private val audioFAbstraction: AudioFAbstraction,
    private val audioRepository: AudioRepository
) {

    fun execute(song: Song, songSelectType: LibraryType): Completable {
        return Completable.fromAction {
            audioRepository.activeType = songSelectType
            audioFAbstraction.song = song
            audioFAbstraction.play()
        }
    }
}
