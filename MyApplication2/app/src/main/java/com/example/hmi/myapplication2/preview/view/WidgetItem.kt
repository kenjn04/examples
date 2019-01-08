package com.example.hmi.myapplication2.preview.view

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.UserHandle
import com.example.hmi.myapplication2.preview.common.LauncherAppWidgetProviderInfo

class WidgetItem(
    val widgetInfo: LauncherAppWidgetProviderInfo,
    packageManager: PackageManager
) {

    val label: String = widgetInfo.getLabel(packageManager)

    // TODO: To be updated
    val spanX:Int = 3
    val spanY:Int = 5

    val componentName: ComponentName
        get() = widgetInfo.provider

    val packageName: String
        get() = componentName.packageName

    val userHandle: UserHandle
        get() = widgetInfo.profile


}