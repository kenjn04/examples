package com.example.hmi.audio.util

import com.example.hmi.audio.common.*
import com.example.hmi.audio.repository.audio.AudioRepository
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository

object SongSelector {

    fun selectPreviousSong(
        currentTrack: Track,
        repeatMode: RepeatMode,
        audioRepository: AudioRepository,
        mediaSourceRepository: MediaSourceRepository
    ): Track? = selectSong(currentTrack, repeatMode, audioRepository, mediaSourceRepository, -1)

    fun selectNextSong(
        currentTrack: Track,
        repeatMode: RepeatMode,
        audioRepository: AudioRepository,
        mediaSourceRepository: MediaSourceRepository
    ): Track? = selectSong(currentTrack, repeatMode, audioRepository, mediaSourceRepository, +1)

    private fun selectSong(
        currentTrack: Track,
        repeatMode: RepeatMode,
        audioRepository: AudioRepository,
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
            RepeatMode.REPEAT, RepeatMode.REPEAT_ALL -> {
                val type = audioRepository.activeType
                val songGroup = mediaSourceRepository.getSpecifiedList(type)!!

                val songList =
                    createSongListToExtractTrack(currentTrack, repeatMode, audioRepository, mediaSourceRepository)

                if (songList != null) {
                    for (i in 1..(songList.size - 2)) {
                        val song = songList.get(i)
                        if (currentTrack.title.equals(song.title)) {
                            return songList.get(i + increment) as Track
                        }
                    }
                }
                return null
            }
        }
    }

    private fun createSongListToExtractTrack(
        currentSong: Track,
        repeatMode: RepeatMode,
        audioRepository: AudioRepository,
        mediaSourceRepository: MediaSourceRepository
    ): TrackList? {
        val type = audioRepository.activeType
        val songGroup = mediaSourceRepository.getSpecifiedList(type)!!
        return when (type) {
            Element.Type.TRACK -> {
                null
            }
            Element.Type.TRACK_LIST -> {
                val songList = TrackList(songGroup as TrackList)
                val songBeginToInsert = songList.get(songGroup.size - 1) as Track
                val songEndToInsert = songList.get(0) as Track
                songList.add(0, songBeginToInsert)
                songList.add(songEndToInsert)
                songList
            }
            Element.Type.ALBUM, Element.Type.ARTISTS, Element.Type.GENRE -> {
                var songList: TrackList? = null
                songGroup as TrackListGroup
                val listNum = songGroup.size
                for (i in 0..(listNum - 1)) {
                    songList = TrackList(songGroup.get(i) as TrackList)
                    if (
                        ((type == Element.Type.ALBUM)   && (currentSong.albumTitle.equals(songList.title))) or
                        ((type == Element.Type.ARTISTS) && (currentSong.artists.equals(songList.title))) or
                        ((type == Element.Type.GENRE)   && (currentSong.genre.equals(songList.title)))
                    ) {
                        if (repeatMode == RepeatMode.REPEAT) {
                            val songBeginToInsert = songList.get(songList.size - 1) as Track
                            val songEndToInsert = songList.get(0) as Track
                            songList.add(0, songBeginToInsert)
                            songList.add(songEndToInsert)
                        } else if (repeatMode == RepeatMode.REPEAT_ALL) {
                            val previousSongList = songGroup.get((i + listNum - 1) % listNum) as TrackList
                            val nextSongList = songGroup.get((i + 1) % listNum) as TrackList
                            val songBeginToInsert = previousSongList.get(previousSongList.size - 1) as Track
                            val songEndToInsert = nextSongList.get(0) as Track
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