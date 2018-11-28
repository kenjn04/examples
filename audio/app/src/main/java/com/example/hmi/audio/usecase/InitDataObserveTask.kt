package com.example.hmi.audio.usecase

import android.arch.lifecycle.MutableLiveData
import android.media.MediaPlayer
import com.example.hmi.audio.common.PlayingSongData
import com.example.hmi.audio.common.RepeatMode
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import com.example.hmi.audio.repository.audio.AudioRepository
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository

import io.reactivex.Single

class InitDataObserveTask(
        private val audioFAbstraction: AudioFAbstraction,
        private val audioRepository: AudioRepository,
        private val mediaSourceRepository: MediaSourceRepository
) {
    fun execute(): Single<OutputData> {
        return Single.fromCallable {
            audioFAbstraction.setOnCompletionListener(MediaPlayerCompletionLister())
            OutputData(
                audioFAbstraction.playingSongData,
                audioRepository.repeatMode
            )
        }
    }

    data class OutputData (
        val playingSongData: MutableLiveData<PlayingSongData>,
        val repeatMode: MutableLiveData<RepeatMode>
    )

    inner class MediaPlayerCompletionLister: MediaPlayer.OnCompletionListener {

        override fun onCompletion(mp: MediaPlayer?) {
            val song = audioFAbstraction.song!!
            val songList = mediaSourceRepository.songList!!

            when (audioRepository.repeatMode.value!!) {
                RepeatMode.NONE -> {
                    // Nothing to do
                }
                RepeatMode.REPEAT_ONE -> {
                    audioFAbstraction.play()
                }
                RepeatMode.REPEAT -> {
                    audioFAbstraction.song = getNextSong(song, songList)
                    audioFAbstraction.play()
                }
            }
        }

        private fun getNextSong(currentSong: Song, songList: MutableList<Song>): Song {
            val num = songList.size
            for (i in 0..(num - 1)) {
                val song = songList.get(i)
                if (song == currentSong) return songList.get((i + 1) % num)
            }
            return currentSong
        }
    }

}
