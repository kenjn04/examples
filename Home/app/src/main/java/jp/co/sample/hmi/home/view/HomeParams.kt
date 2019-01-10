package jp.co.sample.hmi.home.view

import android.graphics.Point

class HomeParams(home: HomeActivity) {

    val displaySize: Point = Point()

    // For WidgetPreviews
    val widgetContainerNum = 5
    val widgetNumInContainerX = 4
    val widgetNumInContainerY = 2

    // For WidgetFrame
    val widgetFrameWidth = 400
    val widgetFrameHeight = 280

    init {
        val display = home.windowManager.defaultDisplay
        display.getSize(displaySize)
    }

}