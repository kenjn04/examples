package jp.co.sample.hmi.home.common

import android.appwidget.AppWidgetProviderInfo
import android.content.pm.PackageManager
import android.os.Parcel

class HomeAppWidgetProviderInfo private constructor(parcel: Parcel)
    : AppWidgetProviderInfo(parcel)
{

    // TODO: Need to be updated. How to retrieve the value?
    val spanX: Int = 2
    val spanY: Int = 2

    fun getLabel(packageManager: PackageManager) = super.loadLabel(packageManager)

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