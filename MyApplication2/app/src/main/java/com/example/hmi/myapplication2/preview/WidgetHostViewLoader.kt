package com.example.hmi.myapplication2.preview

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.hmi.myapplication2.Launcher
import com.example.hmi.myapplication2.preview.common.LauncherAppWidgetProviderInfo
import com.example.hmi.myapplication2.preview.view.WidgetPreviewCell

class WidgetHostViewLoader(
    val launcher: Launcher,
    private val pInfo: LauncherAppWidgetProviderInfo,
    private val id: Int = 0,
    private val x: Int = 0,
    private val y: Int = 0
) {

    private var widgetLoadingId: Int = -1

    fun loadWidget() {
        bindWidget(pInfo)
    }

    private fun bindWidget(pInfo: LauncherAppWidgetProviderInfo) {
        val options = Bundle()

        widgetLoadingId = launcher.appWidgetHost.allocateAppWidgetId()
        if (AppWidgetManager.getInstance(launcher).bindAppWidgetIdIfAllowed(
                widgetLoadingId, pInfo.profile, pInfo.provider, options)
        ) {
            inflateWidget(pInfo)
        } else {
            launcher.requestAppWidgetBind(widgetLoadingId, pInfo, this)
        }
    }

    fun onRequestCompleted(pInfo: LauncherAppWidgetProviderInfo) {
        inflateWidget(pInfo)
    }

    private fun inflateWidget(pInfo: LauncherAppWidgetProviderInfo) {
        val hostView = launcher.appWidgetHost.createView(
            launcher as Context, widgetLoadingId, pInfo
        )
//        info.boundWidget = hostView
        launcher.onAppWidgetInflated(hostView, id, x, y)
    }
}
