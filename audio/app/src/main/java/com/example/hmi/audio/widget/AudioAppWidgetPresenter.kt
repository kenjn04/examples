package com.example.hmi.audio.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import com.example.hmi.audio.repository.audio.AudioRepositoryImpl
import com.example.hmi.audio.repository.mediasource.MediaSourceRepositoryImpl
import com.example.hmi.audio.util.SongSelector

class AudioAppWidgetPresenter(val audioAppWidget: AudioAppWidget) {

    fun requestUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        AudioFAbstraction.getInstance()?.let {
            val song = it.song
            for (appWidgetId in appWidgetIds) {
                audioAppWidget.updateAppWidgetView(context, appWidgetManager, appWidgetId, song)
            }
        }
    }

    fun requestPreviousSong(context: Context, appWidgetId: Int) {
        val audioFAbstraction = AudioFAbstraction.getInstance()
        val audioRepository = AudioRepositoryImpl.getInstance()
        val mediaSourceRepository = MediaSourceRepositoryImpl.getInstance()

        if ((audioFAbstraction != null) and (mediaSourceRepository != null)) {
            val song = audioFAbstraction!!.song
            if (song != null) {
                val previousSong = SongSelector.selectNextSong(song, audioRepository, mediaSourceRepository!!)
                audioFAbstraction.song = previousSong
            }
        }
    }

    fun requestNextSong(context: Context, appWidgetId: Int) {
        val audioFAbstraction = AudioFAbstraction.getInstance()
        val audioRepository = AudioRepositoryImpl.getInstance()
        val mediaSourceRepository = MediaSourceRepositoryImpl.getInstance()

        if ((audioFAbstraction != null) and (mediaSourceRepository != null)) {
            val song = audioFAbstraction!!.song
            if (song != null) {
                val nextSong = SongSelector.selectNextSong(song, audioRepository, mediaSourceRepository!!)
                audioFAbstraction.song = nextSong
            }
        }
    }

}