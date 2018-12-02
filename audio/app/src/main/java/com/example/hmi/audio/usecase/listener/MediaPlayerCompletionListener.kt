package com.example.hmi.audio.usecase.listener

import android.media.MediaPlayer
import com.example.hmi.audio.common.RepeatMode
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import com.example.hmi.audio.repository.audio.AudioRepository
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository
import com.example.hmi.audio.util.SongSelector

class MediaPlayerCompletionListener(
    private val audioFAbstraction: AudioFAbstraction,
    private val audioRepository: AudioRepository,
    private val mediaSourceRepository: MediaSourceRepository
): MediaPlayer.OnCompletionListener {

    override fun onCompletion(mp: MediaPlayer?) {
        val song = audioFAbstraction.song!!
        val repeatMode: RepeatMode = audioRepository.repeatMode.value!!

        when (repeatMode) {
            RepeatMode.NONE -> {
                audioFAbstraction.seek(0)
            }
            RepeatMode.REPEAT_ONE -> {
                audioFAbstraction.play()
            }
            RepeatMode.REPEAT, RepeatMode.REPEAT_ALL -> {
                audioFAbstraction.song =
                        SongSelector.selectNextSong(song, repeatMode, audioRepository, mediaSourceRepository)
                audioFAbstraction.play()
            }
        }
    }
}
