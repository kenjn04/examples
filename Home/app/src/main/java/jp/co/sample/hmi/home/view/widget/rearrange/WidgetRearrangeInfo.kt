package jp.co.sample.hmi.home.view.widget.rearrange

import jp.co.sample.hmi.home.view.widget.WidgetViewCell


data class WidgetInfo(val cId: Int = -1, val x: Int = -1, val y: Int = -1)
data class WidgetRelocateInfo(val widget: WidgetViewCell, val from: WidgetInfo, val to: WidgetInfo)

