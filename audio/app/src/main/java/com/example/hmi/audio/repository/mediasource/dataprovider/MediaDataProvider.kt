package com.example.hmi.audio.repository.mediasource.dataprovider

import com.example.hmi.audio.common.*

abstract class MediaDataProvider {

    abstract var trackList   : TrackList
    abstract var albumList   : TrackListGroup
    abstract var artistsList : TrackListGroup
    abstract var genreList   : TrackListGroup

    open fun createListsOtherThanWholeTrack() {
        createAlbumList()
        createArtistsList()
        createGenreList()
    }

    open fun createAlbumList() {
        for (track in trackList) {
            track as Track
            var found = false
            for (album in albumList) {
                album as TrackList
                if (track.albumTitle.equals(album.title)) {
                    found = true
                    album.add(track)
                    break
                }
            }
            if (!found) albumList.add(TrackList(track.albumTitle).apply {
                add(track)
            })
        }
    }

    open fun createArtistsList() {
        for (track in trackList) {
            track as Track
            var found = false
            for (artists in artistsList) {
                artists as TrackList
                if (track.artists.equals(artists.title)) {
                    found = true
                    artists.add(track)
                    break
                }
            }
            if (!found) artistsList.add(TrackList(track.artists).apply {
                add(track)
            })
        }
    }

    open fun createGenreList() {
        for (track in trackList) {
            track as Track
            var found = false
            for (genre in genreList) {
                genre as TrackList
                if (track.genre.equals(genre.title)) {
                    found = true
                    genre.add(track)
                    break
                }
            }
            if (!found) genreList.add(TrackList(track.genre).apply {
                add(track)
            })
        }
    }

    open fun sortEachList() {
        // TODO: how we should sort each list?
    }

}
