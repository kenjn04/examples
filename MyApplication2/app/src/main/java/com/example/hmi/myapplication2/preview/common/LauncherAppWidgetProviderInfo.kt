package com.example.hmi.myapplication2.preview.common

import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.pm.PackageManager
import android.os.Parcel

class LauncherAppWidgetProviderInfo private constructor(parcel: Parcel)
    : AppWidgetProviderInfo(parcel)
{

    fun getLabel(packageManager: PackageManager) = super.loadLabel(packageManager)

    companion object {
        fun fromProviderInfo(context: Context, info: AppWidgetProviderInfo)
                : LauncherAppWidgetProviderInfo
        {
            val p = Parcel.obtain()
            info.writeToParcel(p, 0)
            p.setDataPosition(0)
            val launcherInfo = LauncherAppWidgetProviderInfo(p)
            p.recycle()
            return launcherInfo
        }
    }

}