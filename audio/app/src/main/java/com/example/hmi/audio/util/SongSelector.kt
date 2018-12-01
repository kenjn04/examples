package com.example.hmi.audio.util

import com.example.hmi.audio.common.Element
import com.example.hmi.audio.common.RepeatMode
import com.example.hmi.audio.common.Track
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository

object SongSelector {

    fun selectPreviousSong(
        currentTrack: Track,
        repeatMode: RepeatMode,
        mediaSourceRepository: MediaSourceRepository
    ): Track? = selectSong(currentTrack, repeatMode, mediaSourceRepository, -1)

    fun selectNextSong(
        currentTrack: Track,
        repeatMode: RepeatMode,
        mediaSourceRepository: MediaSourceRepository
    ): Track? = selectSong(currentTrack, repeatMode, mediaSourceRepository, +1)

    private fun selectSong(
        currentTrack: Track,
        repeatMode: RepeatMode,
        mediaSourceRepository: MediaSourceRepository,
        increment: Int
    ): Track? {
        when (repeatMode) {
            RepeatMode.NONE -> {
                return null
            }
            RepeatMode.REPEAT_ONE -> {
                return currentTrack
            }
            RepeatMode.REPEAT -> {
                val songList = mediaSourceRepository.getSpecifiedList(Element.Type.TRACK_LIST)!!
                val num = songList.size
                for (i in 0..(num - 1)) {
                    val song = songList.get(i) as Track
                    if (song == currentTrack) return songList.get((num + i + increment) % num) as Track
                }
                return currentTrack
            }
        }

    }
}