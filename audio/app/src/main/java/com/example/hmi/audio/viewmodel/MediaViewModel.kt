package com.example.hmi.audio.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Observer
import android.databinding.*
import android.util.Log
import android.view.View
import androidx.navigation.Navigation
import com.example.hmi.audio.R
import com.example.hmi.audio.common.MediaOperation
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.common.SongData
import com.example.hmi.audio.usecase.*

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MediaViewModel(
    application: Application,
    private val getSongListTask: GetSongListTask,
    private val playSongTask: PlaySongTask,
    private val setSongDataObserverTask: SetSongDataObserverTask,
    private val setSongTask: SetSongTask,
    private val stopSongTask: StopSongTask
) : AndroidViewModel(application)
{

    val songList: ObservableList<Song> = ObservableArrayList()

    val track = ObservableField<String>()
    val progress = ObservableInt()
    val duration = ObservableInt()
    val isPlaying = ObservableBoolean()

    init {
        getSongList()
        observeMetadata()
    }

    @SuppressLint("CheckResult")
    private fun observeMetadata() {
        /*
        SetSongDataObserverTask.getSongData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            it.observeForever {
                                setSongData(it!!)
                            }
                        },
                        { error -> Log.d("onError", error.toString()) }
                )
                */
        setSongDataObserverTask.setSongDataUpdateObserver(
                Observer {
                    setSongData(it!!)
                }
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun setSongData(metadata: SongData) {
        track.set(metadata.track)
        duration.set(metadata.duration)
        progress.set(metadata.progress)
        isPlaying.set(metadata.isPlaying)
    }


    fun operateSong(operation: MediaOperation) {
        when (operation) {
            MediaOperation.PLAY -> {
                playSong()
            }
            MediaOperation.STOP -> {
                stopSong()
            }
        }
    }

    @SuppressLint("CheckResult")
    fun playSong() {
        playSongTask.playSong().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {},
                        { error -> Log.d("onError", error.toString()) }
                )
    }

    @SuppressLint("CheckResult")
    fun stopSong() {
        stopSongTask.stopSong().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {},
                        { error -> Log.d("onError", error.toString()) }
                )
    }

    @SuppressLint("CheckResult")
    private fun getSongList() {
        val context = getApplication<Application>().applicationContext
        getSongListTask.getSongList(context).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            songList.addAll(it)
                            Log.d("aaaaaa", songList.size.toString())
                        },
                        {
                            error -> Log.d("onError", error.toString())
                        }
                )
    }

    @SuppressLint("CheckResult")
    fun songSelected(view: View, position: Int) {
        val playingList: List<Song> = songList.subList(0, songList.size)
        setSongTask.setSong(playingList, position).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            Navigation.findNavController(view).navigate(R.id.song_selected)
                        },
                        {
                            error -> Log.d("onError", error.toString())
                        }
                )
    }

    fun goToMenu(view: View) {
        Navigation.findNavController(view).navigate(R.id.to_menu)
    }
}
