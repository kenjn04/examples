package com.example.hmi.audio.repository.audio

import android.arch.lifecycle.MutableLiveData
import com.example.hmi.audio.common.PlayingSongData
import com.example.hmi.audio.common.RepeatMode

class AudioRepositoryImpl private constructor() : AudioRepository {

    override val repeatMode = MutableLiveData<RepeatMode>()

    init {
        // TODO: read from pesistent data
        repeatMode.postValue(RepeatMode.REPEAT)
    }

    override fun incrementRepeatMode() {
        val changedRepeatMode = repeatMode.value!!.increment()
        repeatMode.postValue(changedRepeatMode)
        // TODO: store as persistent data
    }

    companion object {

        @Volatile
        private var INSTANCE: AudioRepositoryImpl? = null
        fun getInstance(): AudioRepository {
            if (INSTANCE == null) {
                synchronized(AudioRepositoryImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE =
                                AudioRepositoryImpl()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
