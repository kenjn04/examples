package com.example.hmi.myapplication2.preview

import android.appwidget.AppWidgetHostView
import com.example.hmi.myapplication2.preview.common.LauncherAppWidgetProviderInfo

class PendingAppWidgetInfo(val info: LauncherAppWidgetProviderInfo) {

    var boundWidget: AppWidgetHostView? = null

    val componentName = info.provider

    val previewImage = info.previewImage

    val icon = info.icon

    fun getHandler() = WidgetAddFlowHandler(info)
}