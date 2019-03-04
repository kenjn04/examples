package jp.co.sample.hmi.home.common

import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.os.Parcel
import android.appwidget.AppWidgetHostView
import android.util.Log


class HomeAppWidgetProviderInfo private constructor(parcel: Parcel)
    : AppWidgetProviderInfo(parcel)
{
    lateinit var widgetLabel: String

    var spanX: Int = 1
    var spanY: Int = 1

    fun initialize(context: Context, minWidgetWidth: Int, minWidgetHeight: Int) {
        val widgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(
                context, provider, null)
        spanX = Math.ceil(
                    (minWidth + widgetPadding.left + widgetPadding.right).toDouble() / minWidgetWidth
                ).toInt()
        spanY = Math.ceil(
                    (minHeight + widgetPadding.top + widgetPadding.bottom).toDouble() / minWidgetHeight
                ).toInt()
        widgetLabel = loadLabel(context.packageManager)
        spanX = 1
        spanY = 2
        Log.d("aaabbbccc", widgetLabel + " " + minWidth + " " + minHeight + " " + minWidgetWidth + " " + minWidgetHeight)
    }

    companion object {

        fun fromProviderInfo(info: AppWidgetProviderInfo)
                : HomeAppWidgetProviderInfo
        {
            val p = Parcel.obtain()
            info.writeToParcel(p, 0)
            p.setDataPosition(0)
            val pInfo = HomeAppWidgetProviderInfo(p)
            p.recycle()
            return pInfo
        }
    }

}