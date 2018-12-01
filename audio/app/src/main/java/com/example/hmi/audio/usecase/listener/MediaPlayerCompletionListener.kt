package com.example.hmi.audio.usecase.listener

import android.media.MediaPlayer
import com.example.hmi.audio.common.RepeatMode
import com.example.hmi.audio.common.Track
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
        val song = audioFAbstraction.track!!
        val repeatMode: RepeatMode = audioRepository.repeatMode.value!!

        when (repeatMode) {
            RepeatMode.NONE -> {
                audioFAbstraction.seek(0)
            }
            RepeatMode.REPEAT_ONE -> {
                audioFAbstraction.play()
            }
            RepeatMode.REPEAT -> {
                audioFAbstraction.track =
                        SongSelector.selectNextSong(song, repeatMode, audioRepository, mediaSourceRepository)
                audioFAbstraction.play()
            }
        }
    }

    private fun getNextSong(currentTrack: Track, trackList: MutableList<Track>): Track {
        val num = trackList.size
        for (i in 0..(num - 1)) {
            val song = trackList.get(i)
            if (song == currentTrack) return trackList.get((i + 1) % num)
        }
        return currentTrack
    }
}
