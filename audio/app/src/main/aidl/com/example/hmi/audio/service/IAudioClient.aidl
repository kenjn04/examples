package com.example.hmi.audio.service;

interface IAudioClient {

    void onMetadataUpdate(String track, int progress, int duration, boolean isPlaying);
}
