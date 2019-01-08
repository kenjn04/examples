package com.example.hmi.myapplication2.common

import android.graphics.Point
import com.example.hmi.myapplication2.Launcher

class LauncherParams(launcher: Launcher) {

    val displaySize: Point = Point()

    // For WidgetPreviewView
    val widgetContainerNum = 5
    val widgetNumInContainerX = 4
    val widgetNumInContainerY = 2

    // For WidgetFrame
    val widgetFrameWidth = 400
    val widgetFrameHeight = 280

    init {
        val display = launcher.windowManager.defaultDisplay
        display.getSize(displaySize)
    }

}