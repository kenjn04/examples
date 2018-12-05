package com.example.hmi.audio.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import com.example.hmi.audio.common.MediaOperation
import com.example.hmi.audio.common.PlayingSongData
import com.example.hmi.audio.common.Song
import com.example.hmi.audio.fabstraction.AudioFAbstraction
import com.example.hmi.audio.repository.audio.AudioRepositoryImpl
import com.example.hmi.audio.repository.mediasource.MediaSourceRepositoryImpl
import com.example.hmi.audio.usecase.SongOperationTask
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AudioWidgetPresenter(private val audioAppWidget: AudioAppWidget) {

    fun requestUpdate(context: Context, appWidgetManager: AppWidgetManager) {
        val audioFAbstraction = AudioFAbstraction.getInstance()
        if (audioFAbstraction != null) {
            updateAppWidgetViews(context, appWidgetManager, audioFAbstraction.playingSongData.value)
            audioFAbstraction.playingSongData.observeForever {
                updateAppWidgetViews(context, appWidgetManager, it)
            }
        }
    }

    private fun updateAppWidgetViews(context: Context, appWidgetManager: AppWidgetManager, playingSongData: PlayingSongData?) {
        if (playingSongData != null) {
            audioAppWidget.updateAppWidgetView(context, appWidgetManager, playingSongData!!)
        }
    }

    @SuppressLint("CheckResult")
    fun operateSong(operation: MediaOperation) {
        val audioFAbstraction = AudioFAbstraction.getInstance()
        val audioRepository = AudioRepositoryImpl.getInstance()
        val mediaSourceRepository = MediaSourceRepositoryImpl.getInstance()
        if ((audioFAbstraction != null) and (mediaSourceRepository != null)) {
            SongOperationTask(audioFAbstraction!!, audioRepository, mediaSourceRepository!!)
                .execute(operation, -1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {},
                    { error -> Log.d("onError", error.toString()) }
                )
        }

    }
}