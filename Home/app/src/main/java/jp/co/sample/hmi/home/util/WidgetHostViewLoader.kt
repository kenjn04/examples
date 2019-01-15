package jp.co.sample.hmi.home.util

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.repository.db.WidgetItemInfo
import jp.co.sample.hmi.home.view.HomeActivity

class WidgetHostViewLoader(
        private val home: HomeActivity,
        private val pInfo: HomeAppWidgetProviderInfo,
        private val item: WidgetItemInfo
) {

    private var widgetLoadingId: Int = -1

    fun loadWidgetView() {
        bindWidget(pInfo)
    }

    private fun bindWidget(pInfo: HomeAppWidgetProviderInfo) {
        val options = Bundle()

        widgetLoadingId = home.appWidgetHost.allocateAppWidgetId()
        if (AppWidgetManager.getInstance(home).bindAppWidgetIdIfAllowed(
                widgetLoadingId, pInfo.profile, pInfo.provider, options)
        ) {
            inflateWidget(pInfo)
        } else {
            home.requestAppWidgetBind(widgetLoadingId, pInfo, this)
        }
    }

    fun onRequestCompleted(pInfo: HomeAppWidgetProviderInfo) {
        inflateWidget(pInfo)
    }

    private fun inflateWidget(pInfo: HomeAppWidgetProviderInfo) {
        val hostView = home.appWidgetHost.createView(
            home as Context, widgetLoadingId, pInfo
        )
        home.onWidgetViewLoaded(hostView, pInfo, item)
    }
}
