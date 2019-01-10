package jp.co.sample.hmi.home.view.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.view.HomeActivity

class WidgetHostViewLoader(
        private val homeActivity: HomeActivity,
        private val pInfo: HomeAppWidgetProviderInfo,
        private val containerId: Int,
        private val coordinateX: Int,
        private val coordinateY: Int
) {

    private var widgetLoadingId: Int = -1

    fun loadWidgetView() {
        bindWidget(pInfo)
    }

    private fun bindWidget(pInfo: HomeAppWidgetProviderInfo) {
        val options = Bundle()

        widgetLoadingId = homeActivity.appWidgetHost.allocateAppWidgetId()
        if (AppWidgetManager.getInstance(homeActivity).bindAppWidgetIdIfAllowed(
                widgetLoadingId, pInfo.profile, pInfo.provider, options)
        ) {
            inflateWidget(pInfo)
        } else {
            homeActivity.requestAppWidgetBind(widgetLoadingId, pInfo, this)
        }
    }

    fun onRequestCompleted(pInfo: HomeAppWidgetProviderInfo) {
        inflateWidget(pInfo)
    }

    private fun inflateWidget(pInfo: HomeAppWidgetProviderInfo) {
        val hostView = homeActivity.appWidgetHost.createView(
            homeActivity as Context, widgetLoadingId, pInfo
        )
//        info.boundWidget = hostView
        homeActivity.onWidgetViewLoaded(hostView, containerId, coordinateX, coordinateY)
    }
}
