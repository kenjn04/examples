package jp.co.nissan.hmi.myapplication.common

import android.appwidget.AppWidgetHostView
import jp.co.nissan.hmi.myapplication.widgethost.WidgetAddFlowHandler

class PendingAppWidgetInfo(val info: LauncherAppWidgetProviderInfo) {

    var boundWidget: AppWidgetHostView? = null

    val componentName = info.provider

    val previewImage = info.previewImage

    val icon = info.icon

    fun getHandler() = WidgetAddFlowHandler(info)
}