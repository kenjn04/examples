package com.example.hmi.audio.util

import com.example.hmi.audio.common.RepeatMode
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository

object SongSelector {

    fun selectPreviousSong(
        currentSong: Song,
        repeatMode: RepeatMode,
        mediaSourceRepository: MediaSourceRepository
    ): Song? = selectSong(currentSong, repeatMode, mediaSourceRepository, -1)

    fun selectNextSong(
        currentSong: Song,
        repeatMode: RepeatMode,
        mediaSourceRepository: MediaSourceRepository
    ): Song? = selectSong(currentSong, repeatMode, mediaSourceRepository, +1)

    private fun selectSong(
        currentSong: Song,
        repeatMode: RepeatMode,
        mediaSourceRepository: MediaSourceRepository,
        increment: Int
    ): Song? {
        when (repeatMode) {
            RepeatMode.NONE -> {
                return null
            }
            RepeatMode.REPEAT_ONE -> {
                return currentSong
            }
            RepeatMode.REPEAT -> {
                val songList = mediaSourceRepository.songList!!
                val num = songList.size
                for (i in 0..(num - 1)) {
                    val song = songList.get(i)
                    if (song == currentSong) return songList.get((num + i + increment) % num)
                }
                return currentSong
            }
        }

    }
}