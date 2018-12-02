package com.example.hmi.audio.repository.mediasource.dataprovider

import com.example.hmi.audio.common.*

abstract class MediaDataProvider {

    abstract var songList : SongList
    abstract var albums   : SongGroup
    abstract var artists  : SongGroup
    abstract var genres   : SongGroup

    open fun createListsOtherThanWholeSongs() {
        createAlbumList()
        createArtistsList()
        createGenreList()
    }

    open fun createAlbumList() {
        for (song in songList) {
            song as Song
            var found = false
            for (album in albums) {
                album as SongList
                if (song.albumTitle.equals(album.title)) {
                    found = true
                    album.add(song)
                    break
                }
            }
            if (!found) albums.add(SongList(song.albumTitle).apply {
                add(song)
            })
        }
    }

    open fun createArtistsList() {
        for (song in songList) {
            song as Song
            var found = false
            for (artists in artists) {
                artists as SongList
                if (song.artists.equals(artists.title)) {
                    found = true
                    artists.add(song)
                    break
                }
            }
            if (!found) artists.add(SongList(song.artists).apply {
                add(song)
            })
        }
    }

    open fun createGenreList() {
        for (song in songList) {
            song as Song
            var found = false
            for (genre in genres) {
                genre as SongList
                if (song.genre.equals(genre.title)) {
                    found = true
                    genre.add(song)
                    break
                }
            }
            if (!found) genres.add(SongList(song.genre).apply {
                add(song)
            })
        }
    }

    open fun sortEachList() {
        // TODO: how we should sort each list?
    }

}
