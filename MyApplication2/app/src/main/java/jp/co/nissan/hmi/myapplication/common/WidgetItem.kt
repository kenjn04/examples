package jp.co.nissan.hmi.myapplication.common

import android.content.ComponentName
import android.os.UserHandle
import android.util.Log
import jp.co.nissan.hmi.myapplication.common.LauncherAppWidgetProviderInfo

class WidgetItem(val widgetInfo: LauncherAppWidgetProviderInfo) {

    val componentName: ComponentName
        get() = widgetInfo.provider

    val packageName: String
        get() = componentName.packageName

    val userHandle: UserHandle
        get() = widgetInfo.profile

}