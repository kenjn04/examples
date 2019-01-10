package jp.co.sample.hmi.home.common

import android.content.ComponentName

data class WidgetItemInfo(
        val componentName: ComponentName,
        val containerId: Int,
        val coordinateX: Int,
        val coordinateY: Int
)