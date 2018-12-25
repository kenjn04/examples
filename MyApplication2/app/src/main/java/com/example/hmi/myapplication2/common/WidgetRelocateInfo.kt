package com.example.hmi.myapplication2.common

import com.example.hmi.myapplication2.WidgetFrame

data class WidgetInfo(val cId: Int = -1, val x: Int = -1, val y: Int = -1)
data class WidgetRelocateInfo(val widget: WidgetFrame, val from: WidgetInfo, val to: WidgetInfo)

