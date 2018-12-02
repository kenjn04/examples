package com.example.hmi.audio.usecase

import com.example.hmi.audio.common.LibraryType
import com.example.hmi.audio.common.SongGroupEntry
import com.example.hmi.audio.common.SongList
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository

import io.reactivex.Single

class SongListObtainTask(
        private val mediaSourceRepository: MediaSourceRepository
) {

    fun execute(type: LibraryType): Single<SongList> {
        return Single.fromCallable {
            mediaSourceRepository.getSpecifiedList(type)
        }
    }
}
