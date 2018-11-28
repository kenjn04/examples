package com.example.hmi.audio.usecase

import com.example.hmi.audio.common.MediaOperation
import com.example.hmi.audio.common.RepeatMode
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import com.example.hmi.audio.repository.audio.AudioRepository
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository
import com.example.hmi.audio.util.SongSelector
import io.reactivex.Completable

class SongOperationTask(
    private val audioFAbstraction: AudioFAbstraction,
    private val audioRepository: AudioRepository,
    private val mediaSourceRepository: MediaSourceRepository
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
                MediaOperation.NEXT_SONG, MediaOperation.PREVIOUS_SONG -> {
                    val song = audioFAbstraction.song!!
                    val repeatMode = audioRepository.repeatMode.value!!

                    if (repeatMode != RepeatMode.NONE) {
                        var nextSong: Song? = null
                        when (operation) {
                            MediaOperation.PREVIOUS_SONG -> {
                                nextSong = SongSelector.selectPreviousSong(song, repeatMode, mediaSourceRepository)
                            }
                            MediaOperation.NEXT_SONG -> {
                                nextSong = SongSelector.selectNextSong(song, repeatMode, mediaSourceRepository)
                            }
                        }
                        audioFAbstraction.song = nextSong
                        audioFAbstraction.play()
                    }
                }
            }
        }
    }
}
