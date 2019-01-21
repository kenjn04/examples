package jp.co.sample.hmi.home.view.widget.rearrange

import jp.co.sample.hmi.home.util.Queue
import jp.co.sample.hmi.home.view.widget.WidgetMap
import jp.co.sample.hmi.home.view.widget.WidgetViewCell

class WidgetMapUpdater(
        private val initialWidgetMap: WidgetMap,
        private val widgetContainerNum: Int,
        private val widgetNumInContainerX: Int,
        private val widgetNumInContainerY: Int
) {

    private val totalX = widgetContainerNum * widgetNumInContainerX
    private val totalY = widgetNumInContainerY

    fun updateWidgetMap(widget: WidgetViewCell, containerId: Int, coordinateX: Int, coordinateY: Int): WidgetMap?
            = calculateWidgetRearrange(widget, containerId, coordinateX, coordinateY)

    // TODO: Update is required
    fun rearrangeWidgetMap(): WidgetMap {
        val rearrangedWidgetMap =  Array(totalX, {arrayOfNulls<WidgetViewCell>(totalY)})
        val rearrangedWidget = mutableSetOf<Int>()
        for (x in 0..(totalX - 1)) {
            for (y in 0..(totalY - 1)) {
                val widgetToRearrange = initialWidgetMap[x][y]
                if ((widgetToRearrange == null) or (rearrangedWidget.contains(widgetToRearrange!!.widgetId))) continue
                rearrangedWidget.add(widgetToRearrange.widgetId)
                for (nx in 0..(totalX - 1)) {
                    var nextX = nx % widgetNumInContainerX
                    var nextId = nx / widgetNumInContainerX
                    if (isWidgetRearrangeable(widgetToRearrange, nextId, nextX, y, rearrangedWidgetMap)) {
                        addWidgetToMap(widgetToRearrange, nextId, nextX, y, rearrangedWidgetMap)
                        break
                    }
                }
            }
        }
        return rearrangedWidgetMap
    }

    data class WidgetRearrangeInfo(val widget: WidgetViewCell, val containerId: Int, val coordinateX: Int, val coordinateY:Int)

    private fun calculateWidgetRearrange(widget: WidgetViewCell, containerId: Int, coordinateX: Int, coordinateY: Int): WidgetMap? {

        val queue = Queue<WidgetRearrangeInfo>()
        val updatedWidgetMap =  Array(totalX, {arrayOfNulls<WidgetViewCell>(totalY)})

        queue.push(WidgetRearrangeInfo(widget, containerId, coordinateX, coordinateY))
        addWidgetToMap(widget, containerId, coordinateX, coordinateY, updatedWidgetMap)

        val rearrangedWidget = mutableSetOf<Int>()
        rearrangedWidget.add(widget.widgetId)
        while (!queue.isEmpty()) {
            val widget = queue.peek().widget
            val cId = queue.peek().containerId
            val toX = queue.peek().coordinateX
            val toY = queue.peek().coordinateY
            queue.pop()

            for (dy in 0..(widget.spanY - 1)) {
                for (dx in 0..(widget.spanX - 1)) {
                    val x = cId * widgetNumInContainerX + toX + dx
                    val y = toY + dy

                    val widgetToRearrange = initialWidgetMap[x][y]
                    if ((widgetToRearrange == null) or (rearrangedWidget.contains(widgetToRearrange!!.widgetId))) continue
                    widgetToRearrange!!
                    rearrangedWidget.add(widgetToRearrange.widgetId)

                    var nextX = toX + widget.spanX
                    var nextY = widgetToRearrange.positionY
                    var nextId = cId
                    while (true) {
                        if (nextX >= widgetNumInContainerX) {
                            nextX = nextX % widgetNumInContainerX
                            nextId++
                            if (nextId >= widgetContainerNum) return null
                        }
                        if (isWidgetRearrangeable(widgetToRearrange, nextId, nextX, nextY, updatedWidgetMap)) {
                            addWidgetToMap(widgetToRearrange, nextId, nextX, nextY, updatedWidgetMap)
                            val info = WidgetRearrangeInfo(widgetToRearrange, nextId, nextX, nextY)
                            queue.push(info)
                            break
                        }
                        nextX++
                    }
                }
            }
        }
        return updatedWidgetMap
    }

    private fun isWidgetRearrangeable(
        widget: WidgetViewCell, containerId: Int, coordinateX: Int, coordinateY: Int, map: WidgetMap
    ): Boolean {
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                if ((coordinateX + dx) >= widgetNumInContainerX) return false
                if ((coordinateY + dy) >= widgetNumInContainerY) return false
                val x = containerId * widgetNumInContainerX + coordinateX + dx
                val y = coordinateY + dy
                if (map[x][y] != null) return false
            }
        }
        return true
    }

    private fun addWidgetToMap(widget: WidgetViewCell, containerId: Int, coordinateX: Int, coordinateY: Int, map: WidgetMap) {
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val x = containerId * widgetNumInContainerX + coordinateX + dx
                val y = coordinateY + dy
                map[x][y] = widget
            }
        }
    }
}