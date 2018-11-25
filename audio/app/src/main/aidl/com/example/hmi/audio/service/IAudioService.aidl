package com.example.hmi.audio.service;

import com.example.hmi.audio.common.Song;
import com.example.hmi.audio.service.IAudioClient;

interface IAudioService {

    void start();

    void pause();

    void seek(int position);

    void setPosition(int position);

    void setSongList(in List<Song> songList);

    boolean registerClient(in IAudioClient client);

    boolean unregisterClient(in IAudioClient client);
}
