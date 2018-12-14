package jp.co.nissan.hmi.myapplication.widgethost

import android.appwidget.AppWidgetHost
import jp.co.nissan.hmi.myapplication.Launcher

class LauncherAppWidgetHost(val launcher: Launcher, hostId: Int): AppWidgetHost(launcher, hostId) {

}