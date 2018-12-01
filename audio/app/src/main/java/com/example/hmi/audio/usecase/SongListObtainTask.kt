package com.example.hmi.audio.usecase

import com.example.hmi.audio.common.Element
import com.example.hmi.audio.common.Track
import com.example.hmi.audio.common.TrackList
import com.example.hmi.audio.repository.mediasource.MediaSourceRepository

import io.reactivex.Single

class SongListObtainTask(
        private val mediaSourceRepository: MediaSourceRepository
) {

    fun execute(type: Element.Type): Single<TrackList> {
        return Single.fromCallable {
            mediaSourceRepository.getSpecifiedList(type)
        }
    }
}
