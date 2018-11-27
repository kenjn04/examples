package com.example.hmi.audio.fabstraction

import com.example.hmi.audio.common.PlayingSongData

interface AudioClient {

    fun onMetadataUpdate(playingSongData: PlayingSongData)
}

