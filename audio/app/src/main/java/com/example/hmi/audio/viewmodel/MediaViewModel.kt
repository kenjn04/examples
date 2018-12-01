package com.example.hmi.audio.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.navigation.Navigation
import com.example.hmi.audio.R
import com.example.hmi.audio.common.*
import com.example.hmi.audio.usecase.*
import com.example.hmi.audio.view.adapter.ElementListAdapter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Thread.sleep
import java.lang.annotation.ElementType
import kotlin.math.E
import kotlin.math.abs
import kotlin.math.max

class MediaViewModel(
    application: Application,
    private val songListObtainTask: SongListObtainTask,
    private val songOperationTask: SongOperationTask,
    private val initialDataObserveTask: InitialDataObserveTask,
    private val songToPlaySetTask: SongToPlaySetTask,
    private val repeatModeIncrementTask: RepeatModeIncrementTask
) : AndroidViewModel(application)
{

    private val SONG_SPEED_CHANGE_WAITTIME_MS = 300L
    private val SONG_SPEED_CHANGE_INTERVAL_MS = 100L

    // This should be more than METADATA_UPDATE_INTERNVAL_MS in MediaPlayerService.
    private val SONG_SPEED_CHANGE_CANCEL_ENDEDGE_MS = 500L

    val trackList = ObservableField<TrackList>()
    private var sourceElementType: Element.Type = Element.Type.TRACK_LIST

    val title = ObservableField<String>()
    val artists = ObservableField<String>()
    val albumTitle = ObservableField<String>()
    val genre = ObservableField<String>()

    val albumArt = ObservableField<Bitmap>()
    val progress = ObservableInt()
    val duration = ObservableInt()
    val elapseTime = ObservableField<String>()
    val isPlaying = ObservableBoolean()

    val repeatMode = ObservableField<String>()

    private val handlerThread = HandlerThread("")

    private val handler: Handler

    init {
        getSongList(Element.Type.TRACK_LIST)
        observeInitData()
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    private var track: Track? = null

    private fun setSongData(playingSongData: PlayingSongData) {
        val playingTrack: Track = playingSongData.playingTrack

        if (playingTrack != track) {
            track = playingTrack

            title.set(playingTrack.title)
            artists.set(playingTrack.artists)
            albumTitle.set(playingTrack.albumTitle)
            genre.set(playingTrack.genre)

            duration.set(playingSongData.duration)
//          duration.set(playingTrack.duration!!.toInt())

            val albumArtByte = playingTrack.albumArt
            if (albumArtByte != null) {
                albumArt.set(BitmapFactory.decodeByteArray(albumArtByte, 0, albumArtByte.size))
            } else {
                albumArt.set(null)
            }
        }
        progress.set(playingSongData.progress)
        isPlaying.set(playingSongData.isPlaying)
        elapseTime.set(formatEpalseTimeToDisplay(progress.get() / 1000, duration.get() / 1000))
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
        handler.postDelayed ({
            while (underSpeedChange) {
                speedChangeInitiated = true
                var progress = progress.get()
                val duration = duration.get()
                if (progress > (duration - SONG_SPEED_CHANGE_CANCEL_ENDEDGE_MS)) {
                    break
                } else {
                    progress += speed / abs(speed) * abs(speed - 1) * SONG_SPEED_CHANGE_INTERVAL_MS.toInt()
                    progress = max(progress, 0)
                    operateSong(MediaOperation.SEEK, progress)
                    sleep(SONG_SPEED_CHANGE_INTERVAL_MS)
                }
            }
            underSpeedChange = false
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
    fun getSongList(type: Element.Type) {
        songListObtainTask.execute(type).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    sourceElementType = Element.Type.TRACK_LIST
                    trackList.set(it)
                },
                { error -> Log.d("onError", error.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    fun songSelected(view: View, track: Track) {
        songToPlaySetTask.execute(track, sourceElementType).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { Navigation.findNavController(view).navigate(R.id.song_selected) },
                    { error -> Log.d("onError", error.toString()) }
            )
    }

    fun elementSelected(view: View, position: Int) {
        val elementList = trackList.get()!!
        val element = elementList.get(position)
        val type = elementList.type
        when (type) {
            Element.Type.TRACK -> {
                // Never Reach Here
            }
            Element.Type.TRACK_LIST -> {
                songSelected(view, element as Track)
            }
            Element.Type.ALBUM -> {
                sourceElementType = Element.Type.ALBUM
                trackList.set(element as TrackList)
            }
            Element.Type.ARTISTS -> {
                sourceElementType = Element.Type.ARTISTS
                trackList.set(element as TrackList)
            }
            Element.Type.GENRE -> {
                sourceElementType = Element.Type.GENRE
                trackList.set(element as TrackList)
            }
        }
    }

    fun goToMenu(view: View) {
        Navigation.findNavController(view).navigate(R.id.to_menu)
    }
}
