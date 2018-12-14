package jp.co.nissan.hmi.myapplication.widgethost

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import jp.co.nissan.hmi.myapplication.Launcher
import jp.co.nissan.hmi.myapplication.common.PendingAppWidgetInfo
import jp.co.nissan.hmi.myapplication.drag.DragController

class WidgetHostViewLoader(
        val launcher: Launcher, val view: View
): DragController.DragListener {

    val info: PendingAppWidgetInfo = view.tag as PendingAppWidgetInfo

    private val handler = Handler()

    var widgetLoadingId: Int = -1

    override fun onDragStart() {
        preloadWidget()
    }

    override fun onDragEnd() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun preloadWidget() {
        val pInfo = info.info

        val options = Bundle()

        val inflateWidgetRunnable = Runnable {
            val hostView = launcher.appWidgetHost.createView(
                    launcher as Context, widgetLoadingId, pInfo
            )
            info.boundWidget = hostView
            launcher.dragLayer.addView(hostView)
        }

        val bindWidgetRunnable = Runnable {
            widgetLoadingId = launcher.appWidgetHost.allocateAppWidgetId()
            if (AppWidgetManager.getInstance(launcher).bindAppWidgetIdIfAllowed(
                            widgetLoadingId, pInfo.profile, pInfo.provider, options)
            ) {
                handler.post(inflateWidgetRunnable)
            }
        }
        handler.post(bindWidgetRunnable)
    }
}
