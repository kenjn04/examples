package com.example.hmi.audio.repository.mediasource.dataprovider

import com.example.hmi.audio.common.Song

interface MediaDataProvider {

    var songList: ArrayList<Song>?
}
