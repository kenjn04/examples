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

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Thread.sleep
import kotlin.math.abs
import kotlin.math.max

class MediaViewModel(
    application: Application,
    private val songListObtainTask: SongListObtainTask,
    private val songOperationTask: SongOperationTask,
    private val initialDataObserveTask: InitialDataObserveTask,
    private val songToPlaySetTask: SongToPlaySetTask,
    private val repeatModeIncrementTask: RepeatModeIncrementTask
) : AndroidViewModel(application) {
    /**
     * Intervals for song speed change (fast forward, rewind)
     */
    private val SONG_SPEED_CHANGE_WAITTIME_MS = 300L
    private val SONG_SPEED_CHANGE_INTERVAL_MS = 100L
    // The below should be more than METADATA_UPDATE_INTERNVAL_MS in MediaPlayerService.
    private val SONG_SPEED_CHANGE_CANCEL_ENDEDGE_MS = 500L
    /**
     * Thread for song speed change (fast forward, rewind)
     */
    private val handlerThread = HandlerThread("")
    private val handler: Handler
    /**
     * Current status for speed change (fast forward, rewind)
     */
    private var underSpeedChange = false
    private var speedChangeInitiated = false


    /**
     * SongGroup information
     */
    // Current displayed SongGroup (song list, ablum list, artist list and genre list)
    val songGroup = ObservableField<SongList>()
    // From where the song is selected to play
    private var sourceSongGroupEntryType = LibraryType.SONGS

    /**
     * Current playing song information
     */
    private var song: Song? = null
    // playing song metadata
    val title = ObservableField<String>()
    val artists = ObservableField<String>()
    val albumTitle = ObservableField<String>()
    val genre = ObservableField<String>()
    val albumArt = ObservableField<Bitmap>()
    val progress = ObservableInt()
    val duration = ObservableInt()
    val elapseTime = ObservableField<String>()

    val isPlaying = ObservableBoolean()

    /**
     * Current repeat mode
     */
    val repeatMode = ObservableField<String>()

    init {
        getSongList(LibraryType.SONGS)
        observeInitData()
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    private fun setSongData(playingSongData: PlayingSongData) {
        val playingSong: Song = playingSongData.playingSong

        // When playing song is changed
        if (playingSong != song) {
            song = playingSong

            title.set(playingSong.title)
            artists.set(playingSong.artists)
            albumTitle.set(playingSong.albumTitle)
            genre.set(playingSong.genre)

            duration.set(playingSongData.duration)
//          duration.set(playingSong.duration!!.toInt())

            val albumArtByte = playingSong.albumArt
            if (albumArtByte != null) {
                albumArt.set(BitmapFactory.decodeByteArray(albumArtByte, 0, albumArtByte.size))
            } else {
                albumArt.set(null)
            }
        }

        progress.set(playingSongData.progress)
        isPlaying.set(playingSongData.isPlaying)
        elapseTime.set(formatElapseTimeToDisplay(progress.get() / 1000, duration.get() / 1000))
    }

    private fun formatElapseTimeToDisplay(progress: Int, duration: Int): String {
        return getApplication<Application>().applicationContext
            .getString(
                R.string.media_elapse_format,
                progress / 60,
                progress % 60,
                duration / 60,
                duration % 60
            )
    }

    /**
     * Observe required data for this ViewModel
     */
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

    /**
     * For fast forward and rewind
     */
    fun startSongSpeedChange(speed: Int) {
        underSpeedChange = true
        speedChangeInitiated = false
        handler.postDelayed({
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
    fun getSongList(type: LibraryType) {
        songListObtainTask.execute(type).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    sourceSongGroupEntryType = LibraryType.SONGS
                    songGroup.set(it)
                },
                { error -> Log.d("onError", error.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    fun songSelected(view: View, song: Song) {
        songToPlaySetTask.execute(song, sourceSongGroupEntryType).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Navigation.findNavController(view).navigate(R.id.song_selected) },
                { error -> Log.d("onError", error.toString()) }
            )
    }

    fun songGroupEntrySelected(view: View, position: Int) {
        val songGroup = this.songGroup.get()!!
        val entry = songGroup.get(position)
        val type = songGroup.type
        when (type) {
            LibraryType.SONGS -> {
                songSelected(view, entry as Song)
            }
            LibraryType.ALBUMS, LibraryType.ARTISTS, LibraryType.GENRES -> {
                sourceSongGroupEntryType = type
                this.songGroup.set(entry as SongList)
            }
        }
    }

    fun goToMenu(view: View) {
        Navigation.findNavController(view).navigate(R.id.to_menu)
    }
}
