package com.example.hmi.audio.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.*
import android.util.Log
import android.view.View
import androidx.navigation.Navigation
import com.example.hmi.audio.R
import com.example.hmi.audio.common.MediaOperation
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.common.PlayingSongData
import com.example.hmi.audio.usecase.*

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MediaViewModel(
    application: Application,
    private val getSongListTask: GetSongListTask,

    private val songOperationTask: SongOperationTask,
    private val playingSongObserveTask: PlayingSongObserveTask,
    private val songToPlaySetTask: SongToPlaySetTask
) : AndroidViewModel(application)
{

    val songList: ObservableList<Song> = ObservableArrayList()

    val title = ObservableField<String>()
    val progress = ObservableInt()
    val duration = ObservableInt()
    val isPlaying = ObservableBoolean()

    init {
        getSongList()
        observePlayingSong()
    }

    private fun setSongData(playingSongData: PlayingSongData) {
        val playingSong: Song = playingSongData.playingSong
        title.set(playingSong.title)
        duration.set(playingSong.duration!!.toInt())
        progress.set(playingSongData.progress)
        isPlaying.set(playingSongData.isPlaying)
        Log.d("aaaaa", playingSongData.isPlaying.toString() )
    }

    @SuppressLint("CheckResult")
    private fun observePlayingSong() {
        playingSongObserveTask.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    it.observeForever {
                        setSongData(it!!)
                    }
                },
                { error -> Log.d("onError", error.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    fun operateSong(operation: MediaOperation) {
        songOperationTask.execute(operation)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {},
                { error -> Log.d("onError", error.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    private fun getSongList() {
        getSongListTask.getSongList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { songList.addAll(it) },
                { error -> Log.d("onError", error.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    fun songSelected(view: View, position: Int) {
        val a = songList.get(position)
        songToPlaySetTask.execute(songList.get(position)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { Navigation.findNavController(view).navigate(R.id.song_selected) },
                        { error -> Log.d("onError", error.toString()) }
                )
    }

    fun goToMenu(view: View) {
        Navigation.findNavController(view).navigate(R.id.to_menu)
    }
}
