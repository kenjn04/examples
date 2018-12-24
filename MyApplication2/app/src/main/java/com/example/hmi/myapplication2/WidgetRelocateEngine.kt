package com.example.hmi.myapplication2

import com.example.hmi.myapplication2.util.Queue

typealias WidgetMap = Array<Array<WidgetFrame?>>

class WidgetRelocateEngine(
    private val originalWidgetMap: WidgetMap,
    private val widgetContainerNum: Int,
    private val widgetNumInContainerX: Int,
    private val widgetNumInContainerY: Int
) {

    private lateinit var pendingWidgetMap: WidgetMap

    var widgetsToRelocate: MutableList<WidgetRelocateInfo>? = null

    private fun initPendingWidgetMap() {
        val totalX = widgetContainerNum * widgetNumInContainerX
        val totalY = widgetNumInContainerY
        pendingWidgetMap = Array(totalX, {arrayOfNulls<WidgetFrame>(totalY)})
    }

    fun isWidgetDroppable(widget: WidgetFrame, cId: Int, toX: Int, toY: Int): Boolean {
        initPendingWidgetMap()
        widgetsToRelocate = mutableListOf()
        if (calculateRearrangeWidget(widget, cId, toX, toY)) {
            return true
        } else {
            widgetsToRelocate = null
            return false
        }
    }

    data class WidgetRelocateInfo(val widget: WidgetFrame, val cId: Int, val toX: Int, val toY: Int)

    private fun calculateRearrangeWidget(widget: WidgetFrame, cId: Int, toX: Int, toY: Int): Boolean {

        val queue = Queue<WidgetRelocateInfo>()
        queue.push(WidgetRelocateInfo(widget, cId, toX, toY))
        addWidgetToMap(widget, cId, toX, toY, pendingWidgetMap)

        val relocatedWidget = mutableSetOf<WidgetFrame>()
        relocatedWidget.add(widget)
        while (!queue.isEmpty()) {
            val widget = queue.peek().widget
            val cId = queue.peek().cId
            val toX = queue.peek().toX
            val toY = queue.peek().toY
            queue.pop()

            for (dy in 0..(widget.spanY - 1)) {
                for (dx in 0..(widget.spanX - 1)) {
                    val x = cId * widgetNumInContainerX + toX + dx
                    val y = toY + dy

                    val locatedWidget = originalWidgetMap[x][y]
                    if ((locatedWidget == null) or (relocatedWidget.contains(locatedWidget))) continue
                    locatedWidget!!
                    relocatedWidget.add(locatedWidget)

                    var newX = toX + widget.spanX
                    var newId = cId
                    while (true) {
                        if (isWidgetLocatable(locatedWidget, newId, newX, y, pendingWidgetMap)) {
                            addWidgetToMap(locatedWidget, newId, newX, y, pendingWidgetMap)
                            val info = WidgetRelocateInfo(locatedWidget, newId, newX, y)
                            queue.push(info)
                            widgetsToRelocate!!.add(info)
                            break
                        }
                        newX++
                        if (newX >= widgetNumInContainerX) {
                            newX = newX % widgetNumInContainerX
                            newId++
                            if (newId >= widgetContainerNum) return false
                        }
                    }
                }
            }
        }
        return true
    }

    private fun isWidgetLocatable(
        widget: WidgetFrame, cId: Int, toX: Int, toY: Int, map: WidgetMap
    ): Boolean {
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                if ((toX + dx) >= widgetNumInContainerX) return false
                if ((toY + dy) >= widgetNumInContainerY) return false
                val x = cId * widgetNumInContainerX + toX + dx
                val y = toY + dy
                if (map[x][y] != null) return false
            }
        }
        return true
    }

    private fun addWidgetToMap(widget: WidgetFrame, cId: Int, toX: Int, toY: Int, map: WidgetMap) {
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val x = cId * widgetNumInContainerX + toX + dx
                val y = toY + dy
                map[x][y] = widget
            }
        }
    }

}