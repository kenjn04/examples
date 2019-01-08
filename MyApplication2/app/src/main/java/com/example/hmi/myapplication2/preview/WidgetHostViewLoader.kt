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
    val launcher: Launcher, val view: WidgetPreviewCell
) {

    val info: PendingAppWidgetInfo = view.tag as PendingAppWidgetInfo

    private var widgetLoadingId: Int = -1

    fun loadWidget() {
        val pInfo = info.info

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

    private fun inflateWidget(pInfo: LauncherAppWidgetProviderInfo) {
        val hostView = launcher.appWidgetHost.createView(
            launcher as Context, widgetLoadingId, pInfo
        )
        info.boundWidget = hostView
        launcher.onAppWidgetInflated(hostView)
    }

    fun onRequestCompleted(pInfo: LauncherAppWidgetProviderInfo) {
        inflateWidget(pInfo)
    }
}
