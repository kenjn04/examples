package com.example.hmi.audio.util

import com.example.hmi.audio.common.*
import com.example.hmi.audio.repository.audio.AudioRepository
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository

object SongSelector {

    fun selectPreviousSong(
        currentSong: Song,
        repeatMode: RepeatMode,
        audioRepository: AudioRepository,
        mediaSourceRepository: MediaSourceRepository
    ): Song? = selectSong(currentSong, repeatMode, audioRepository, mediaSourceRepository, -1)

    fun selectNextSong(
        currentSong: Song,
        repeatMode: RepeatMode,
        audioRepository: AudioRepository,
        mediaSourceRepository: MediaSourceRepository
    ): Song? = selectSong(currentSong, repeatMode, audioRepository, mediaSourceRepository, +1)

    private fun selectSong(
        currentSong: Song,
        repeatMode: RepeatMode,
        audioRepository: AudioRepository,
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
            RepeatMode.REPEAT, RepeatMode.REPEAT_ALL -> {
                val songList =
                    createSongListToExtractSong(currentSong, repeatMode, audioRepository, mediaSourceRepository)

                if (songList != null) {
                    for (i in 1..(songList.size - 2)) {
                        val song = songList.get(i)
                        if (currentSong.title.equals(song.title)) {
                            return songList.get(i + increment) as Song
                        }
                    }
                }
                return null
            }
        }
    }

    private fun createSongListToExtractSong(
        currentSong: Song,
        repeatMode: RepeatMode,
        audioRepository: AudioRepository,
        mediaSourceRepository: MediaSourceRepository
    ): SongList? {
        val type = audioRepository.activeType
        val songGroup = mediaSourceRepository.getSpecifiedList(type)!!
        return when (type) {
            LibraryType.SONGS -> {
                val songList = SongList(songGroup)
                val songBeginToInsert = songList.get(songGroup.size - 1) as Song
                val songEndToInsert = songList.get(0) as Song
                songList.add(0, songBeginToInsert)
                songList.add(songEndToInsert)
                songList
            }
            LibraryType.ALBUMS, LibraryType.ARTISTS, LibraryType.GENRES -> {
                var songList: SongList? = null
                songGroup as SongGroup
                val listNum = songGroup.size
                for (i in 0..(listNum - 1)) {
                    songList = SongList(songGroup.get(i) as SongList)
                    if (
                        ((type == LibraryType.ALBUMS)  && (currentSong.albumTitle.equals(songList.title))) or
                        ((type == LibraryType.ARTISTS) && (currentSong.artists.equals(songList.title))) or
                        ((type == LibraryType.GENRES)  && (currentSong.genre.equals(songList.title)))
                    ) {
                        if (repeatMode == RepeatMode.REPEAT) {
                            val songBeginToInsert = songList.get(songList.size - 1) as Song
                            val songEndToInsert = songList.get(0) as Song
                            songList.add(0, songBeginToInsert)
                            songList.add(songEndToInsert)
                        } else if (repeatMode == RepeatMode.REPEAT_ALL) {
                            val previousSongList = songGroup.get((i + listNum - 1) % listNum) as SongList
                            val nextSongList = songGroup.get((i + 1) % listNum) as SongList
                            val songBeginToInsert = previousSongList.get(previousSongList.size - 1) as Song
                            val songEndToInsert = nextSongList.get(0) as Song
                            songList.add(0, songBeginToInsert)
                            songList.add(songEndToInsert)
                        }
                        break
                    }
                }
                songList
            }
        }
    }
}