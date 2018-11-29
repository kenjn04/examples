package com.example.hmi.audio.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.navigation.Navigation
import com.example.hmi.audio.R
import com.example.hmi.audio.common.MediaOperation
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.common.PlayingSongData
import com.example.hmi.audio.usecase.*

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Thread.sleep
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MediaViewModel(
    application: Application,
    private val songListObtainTask: SongListObtainTask,
    private val songOperationTask: SongOperationTask,
    private val initialDataObserveTask: InitialDataObserveTask,
    private val songToPlaySetTask: SongToPlaySetTask,
    private val repeatModeIncrementTask: RepeatModeIncrementTask
) : AndroidViewModel(application)
{

    private val SONG_SPEED_CHANGE_WAITTIME_MS: Long = 300
    private val SONG_SPEED_CHANGE_INTERVAL_MS: Long = 100

    val songList: ObservableList<Song> = ObservableArrayList()

    val title = ObservableField<String>()
    val progress = ObservableInt()
    val duration = ObservableInt()
    val elapseTime = ObservableField<String>()
    val isPlaying = ObservableBoolean()

    val repeatMode = ObservableField<String>()

    private val handlerThread = HandlerThread("")

    private val handler: Handler

    init {
        getSongList()
        observeInitData()
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    private fun setSongData(playingSongData: PlayingSongData) {
        val playingSong: Song = playingSongData.playingSong
        title.set(playingSong.title)
//        duration.set(playingSong.duration!!.toInt())
        duration.set(playingSongData.duration)
        progress.set(playingSongData.progress)
        isPlaying.set(playingSongData.isPlaying)
        elapseTime.set(formatEpalseTimeToDisplay(progress.get() / 1000, duration.get() / 1000))
        Log.d("aaaaa", playingSongData.isPlaying.toString() + " " + playingSongData.duration + " " + playingSongData.progress)
    }

    private fun formatEpalseTimeToDisplay(progress: Int, duration: Int): String {
        return getApplication<Application>().applicationContext
            .getString(R.string.media_elapse_format,
                progress / 60,
                progress % 60,
                duration / 60,
                duration % 60
            )
    }

    @SuppressLint("CheckResult")
    private fun observeInitData() {
        initialDataObserveTask.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    it.playingSongData.observeForever {
                        setSongData(it!!)
                    }
                    it.repeatMode.observeForever {
                        repeatMode.set(it!!.toString())
                    }
                },
                { error -> Log.d("onError", error.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    private fun operateSong(operation: MediaOperation, progress: Int) {
        songOperationTask.execute(operation, progress)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {},
                { error -> Log.d("onError", error.toString()) }
            )
    }

    fun operateSong(operation: MediaOperation) {
        operateSong(operation, -1)
    }

    fun songProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            operateSong(MediaOperation.SEEK, progress)
        }
    }

    private var underSpeedChange = false
    private var speedChangeInitiated = false

    fun startSongSpeedChange(speed: Int) {
        underSpeedChange = true
        speedChangeInitiated = false
        // TODO: consider to seek in end of the song.
        handler.postDelayed ({
            while (underSpeedChange) {
                speedChangeInitiated = true
                var progress = progress.get()
                val duration = duration.get()
                progress += speed / abs(speed) * abs(speed - 1) * SONG_SPEED_CHANGE_INTERVAL_MS.toInt()
                progress = max(min(progress, duration), 0)
                operateSong(MediaOperation.SEEK, progress)
                sleep(SONG_SPEED_CHANGE_INTERVAL_MS)
            }
        }, SONG_SPEED_CHANGE_WAITTIME_MS)
    }

    fun cancelSongSpeedChange(): Boolean {
        underSpeedChange = false
        return speedChangeInitiated
    }

    @SuppressLint("CheckResult")
    fun incrementRepeatMode() {
        repeatModeIncrementTask.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {},
                { error -> Log.d("onError", error.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    private fun getSongList() {
        songListObtainTask.execute().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { songList.addAll(it) },
                { error -> Log.d("onError", error.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    fun songSelected(view: View, position: Int) {
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
