package jp.co.sample.hmi.home.common

import android.appwidget.AppWidgetProviderInfo
import android.os.Parcel

class HomeAppWidgetProviderInfo private constructor(parcel: Parcel)
    : AppWidgetProviderInfo(parcel)
{

    // TODO: Need to be updated. How to retrieve the value?
    val widgetLabel: String = "AAAAA"
    val spanX: Int = 2
    val spanY: Int = 2

    companion object {
        fun fromProviderInfo(info: AppWidgetProviderInfo)
                : HomeAppWidgetProviderInfo
        {
            val p = Parcel.obtain()
            info.writeToParcel(p, 0)
            p.setDataPosition(0)
            val launcherInfo = HomeAppWidgetProviderInfo(p)
            p.recycle()
            return launcherInfo
        }
    }

}