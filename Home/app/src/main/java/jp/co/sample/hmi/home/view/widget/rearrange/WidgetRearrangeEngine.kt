package jp.co.sample.hmi.home.view.widget.rearrange

import jp.co.sample.hmi.home.util.Queue
import jp.co.sample.hmi.home.view.widget.WidgetMap
import jp.co.sample.hmi.home.view.widget.WidgetViewCell

class WidgetRearrangeEngine(
        private val originalWidgetMap: WidgetMap,
        private val widgetContainerNum: Int,
        private val widgetNumInContainerX: Int,
        private val widgetNumInContainerY: Int
) {

    private lateinit var pendingWidgetMap: WidgetMap

    var widgetsToRelocate: MutableList<WidgetRelocateInfo>? = null

    private val originalWidgetInfo = mutableMapOf<WidgetViewCell, WidgetInfo>()

    private fun initPendingWidgetMap() {
        val totalX = widgetContainerNum * widgetNumInContainerX
        val totalY = widgetNumInContainerY
        pendingWidgetMap = Array(totalX, {arrayOfNulls<WidgetViewCell>(totalY)})
    }

    fun isWidgetDroppable(widget: WidgetViewCell, cId: Int, toX: Int, toY: Int): Boolean {
        initPendingWidgetMap()
        widgetsToRelocate = mutableListOf()
        if (calculateRearrangeWidget(widget, cId, toX, toY)) {
            return true
        } else {
            widgetsToRelocate = null
            return false
        }
    }

    private fun calculateRearrangeWidget(widget: WidgetViewCell, cId: Int, toX: Int, toY: Int): Boolean {

        val queue = Queue<WidgetRelocateInfo>()
        queue.push(WidgetRelocateInfo(widget, WidgetInfo(), WidgetInfo(cId, toX, toY)))
        addWidgetToMap(widget, cId, toX, toY, pendingWidgetMap)

        val relocatedWidget = mutableSetOf<WidgetViewCell>()
        relocatedWidget.add(widget)
        while (!queue.isEmpty()) {
            val widget = queue.peek().widget
            val cId = queue.peek().to.cId
            val toX = queue.peek().to.x
            val toY = queue.peek().to.y
            queue.pop()

            for (dy in 0..(widget.spanY - 1)) {
                for (dx in 0..(widget.spanX - 1)) {
                    val x = cId * widgetNumInContainerX + toX + dx
                    val y = toY + dy

                    val locatedWidget = originalWidgetMap[x][y]
                    if ((locatedWidget == null) or (relocatedWidget.contains(locatedWidget))) continue
                    locatedWidget!!
                    relocatedWidget.add(locatedWidget)

                    if (originalWidgetInfo.get(locatedWidget) == null) {
                        originalWidgetInfo.put(locatedWidget,
                                WidgetInfo(cId, locatedWidget.positionX, locatedWidget.positionY)
                        )
                    }

                    var nextX = toX + widget.spanX
                    var nextY = locatedWidget.positionY
                    var nextId = cId
                    while (true) {
                        if (nextX >= widgetNumInContainerX) {
                            nextX = nextX % widgetNumInContainerX
                            nextId++
                            if (nextId >= widgetContainerNum) return false
                        }
                        if (isWidgetLocatable(locatedWidget, nextId, nextX, nextY, pendingWidgetMap)) {
                            addWidgetToMap(locatedWidget, nextId, nextX, nextY, pendingWidgetMap)
                            val info = WidgetRelocateInfo(locatedWidget, originalWidgetInfo.get(locatedWidget)!!, WidgetInfo(nextId, nextX, nextY))
                            queue.push(info)
                            widgetsToRelocate!!.add(info)
                            break
                        }
                        nextX++
                    }
                }
            }
        }
        return true
    }

    private fun isWidgetLocatable(
        widget: WidgetViewCell, cId: Int, toX: Int, toY: Int, map: WidgetMap
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

    private fun addWidgetToMap(widget: WidgetViewCell, cId: Int, toX: Int, toY: Int, map: WidgetMap) {
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val x = cId * widgetNumInContainerX + toX + dx
                val y = toY + dy
                map[x][y] = widget
            }
        }
    }
}