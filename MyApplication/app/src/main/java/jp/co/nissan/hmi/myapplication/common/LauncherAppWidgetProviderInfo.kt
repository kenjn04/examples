package jp.co.nissan.hmi.myapplication.common

import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.os.Parcel

class LauncherAppWidgetProviderInfo private constructor(parcel: Parcel)
    : AppWidgetProviderInfo(parcel)
{

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