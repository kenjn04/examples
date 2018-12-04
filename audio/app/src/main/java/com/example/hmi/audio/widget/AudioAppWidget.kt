package com.example.hmi.audio.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import com.example.hmi.audio.R
import com.example.hmi.audio.common.MediaOperation
import com.example.hmi.audio.common.Song

/**
 * Implementation of App Widget functionality.
 */
class AudioAppWidget : AppWidgetProvider() {

    private val audioWidgetPresenter: AudioWidgetPresenter = AudioWidgetPresenter(this)

    private val REQUEST_TO_PREVIOUS_SONG = "REQUEST_TO_PREVIOUS_SONG"
    private val REQUEST_TO_NEXT_SONG     = "REQUEST_TO_NEXT_SONG"

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        audioWidgetPresenter.requestUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if ((intent != null) and (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID)) {
            when (intent!!.action) {
                REQUEST_TO_PREVIOUS_SONG -> {
                    audioWidgetPresenter.operateSong(MediaOperation.PREVIOUS_SONG)
                }
                REQUEST_TO_NEXT_SONG -> {
                    audioWidgetPresenter.operateSong(MediaOperation.NEXT_SONG)
                }
            }
        }
        super.onReceive(context, intent)
    }

    fun updateAppWidgetView(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        song: Song?
    ) {

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.audio_app_widget)

        if (song != null) {
            val albumArtByte = song.albumArt
            if (albumArtByte != null) {
                val bitmap = BitmapFactory.decodeByteArray(albumArtByte, 0, albumArtByte.size)
                views.setImageViewBitmap(R.id.album_art_image, bitmap)
            }
            views.setTextViewText(R.id.song_title, song.title)
            views.setTextViewText(R.id.album_title, song.albumTitle)
            views.setOnClickPendingIntent(
                R.id.previous_song_button,
                getPendingIntent(context, appWidgetId, REQUEST_TO_PREVIOUS_SONG)
            )
            views.setOnClickPendingIntent(
                R.id.next_song_button,
                getPendingIntent(context, appWidgetId, REQUEST_TO_NEXT_SONG)
            )
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPendingIntent(context: Context, appWidgetId: Int, action: String): PendingIntent {
        val intent = Intent(context, AudioAppWidget::class.java).apply {
            this.action = action
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        return PendingIntent.getBroadcast(context, appWidgetId, intent, 0)
    }
}

