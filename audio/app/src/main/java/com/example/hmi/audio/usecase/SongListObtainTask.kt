package com.example.hmi.audio.usecase

import com.example.hmi.audio.common.Song
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository

import io.reactivex.Single

class SongListObtainTask(
        private val mediaSourceRepository: MediaSourceRepository
) {

    fun execute(): Single<MutableList<Song>> {
        return Single.fromCallable { mediaSourceRepository.songList }
    }
}
