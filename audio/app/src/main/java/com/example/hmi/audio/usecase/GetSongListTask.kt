package com.example.hmi.audio.usecase

import com.example.hmi.audio.common.Song
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository

import io.reactivex.Single

class GetSongListTask(
        private val mediaSourceRepository: MediaSourceRepository
) {

    fun execute(): Single<ArrayList<Song>> {
        return Single.fromCallable { mediaSourceRepository.songList }
    }
}
