package com.example.hmi.audio.fabstraction

interface AudioClient {

    fun onMetadataUpdate(track: String, progress: Int, duration: Int, isPlaying: Boolean);
}

