package com.example.hmi.audio.usecase

import android.content.Context
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.repository.MediaSourceRepository

import io.reactivex.Single

class GetSongListTask(
        private val mediaSourceRepository: MediaSourceRepository
) {

    fun getSongList(): Single<ArrayList<Song>> {
        return Single.fromCallable { mediaSourceRepository.getSongList() }
    }
}
