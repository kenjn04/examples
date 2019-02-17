package jp.co.sample.hmi.home.view.widget.rearrange

import android.util.Log
import jp.co.sample.hmi.home.util.Queue
import jp.co.sample.hmi.home.view.widget.WidgetMap
import jp.co.sample.hmi.home.view.widget.WidgetViewCell
import kotlin.math.abs

class WidgetMapUpdater(
        private val initialWidgetMap: WidgetMap,
        private val widgetContainerNum: Int,
        private val widgetNumInContainerX: Int,
        private val widgetNumInContainerY: Int
) {

    private val TAG = "WidgetMapUpdator"

    private val totalX = widgetContainerNum * widgetNumInContainerX
    private val totalY = widgetNumInContainerY

    data class WidgetRearrangeInfo(val widget: WidgetViewCell, val containerId: Int, val coordinateX: Int, val coordinateY:Int)

    fun updateWidgetMap(widget: WidgetViewCell, containerId: Int, coordinateX: Int, coordinateY: Int): WidgetMap? {

        /** copy the initial WidgetMap */
        var latestMap = copyMap(initialWidgetMap)

        /** remove widget to be moved from latestMap */
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val absoluteX = containerId * widgetNumInContainerX + coordinateX + dx
                val absoluteY = coordinateY + dy
                latestMap[absoluteX][absoluteY] = null
            }
        }

        /** First, try to move widget to left as much as possible */
        val moveLeftSuccessWidgets = mutableSetOf<Int>()

        val containerIdToMove = if (coordinateX != 0) containerId else containerId - 1
        if (containerIdToMove >= 0) {

            val coordinateXToMove = if (coordinateX != 0) coordinateX - 1 else widgetNumInContainerX - 1
            val canBeMovedLeftY = mutableListOf<Boolean>()
            for (i in 0..(widget.spanY - 1)) canBeMovedLeftY.add(true)

            val rearrangedWidget = mutableSetOf<Int>()
            for (dx in 0..(widget.spanX - 1)) {
                for (dy in 0..(widget.spanY - 1)) {
                    if (!canBeMovedLeftY[dy]) continue
                    val absoluteX = containerId * widgetNumInContainerX + coordinateX + dx
                    val absoluteY = coordinateY + dy
                    val widgetToRearrange = initialWidgetMap[absoluteX][absoluteY] ?: continue

                    if (rearrangedWidget.contains(widgetToRearrange.widgetId)) continue
                    rearrangedWidget.add(widgetToRearrange.widgetId)

                    /** if the left edge is out of scope of widget new location, skip to move left */
                    if ((widgetToRearrange.positionX + widgetToRearrange.spanX) > (widget.positionX + widget.spanX)) {
                        canBeMovedLeftY[dy] = false
                        continue
                    }

                    /** move widget and update latest Map */
                    val newMap =
                        calculateWidgetRearrange(latestMap, widgetToRearrange, containerIdToMove, coordinateXToMove, coordinateY, false)

                    if (newMap != null) {
                        latestMap = newMap
                        moveLeftSuccessWidgets.add(widgetToRearrange.widgetId)
                    } else {
                        canBeMovedLeftY[dy] = false
                    }
                }
            }
        }

        /** prepare to make remaining widget move to right */
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val absoluteX = containerId * widgetNumInContainerX + coordinateX + dx
                val absoluteY = coordinateY + dy
                val widgetToReflect = initialWidgetMap[absoluteX][absoluteY] ?: continue
                if (moveLeftSuccessWidgets.contains(widgetToReflect.widgetId)) continue
                latestMap[absoluteX][absoluteY] = widgetToReflect
            }
        }

        /** Move remaining widgets to right */
        return calculateWidgetRearrange(latestMap, widget, containerId, coordinateX, coordinateY, true)
    }

    private fun copyMap(source: WidgetMap): WidgetMap {
        val newMap =  Array(totalX, {arrayOfNulls<WidgetViewCell>(totalY)})
        for (x in 0..(widgetContainerNum * widgetNumInContainerX - 1)) {
            for (y in 0..(widgetNumInContainerY - 1)) {
                newMap[x][y] = source[x][y]
            }
        }
        return newMap
    }

    private fun calculateWidgetRearrange(widgetMap: WidgetMap, widget: WidgetViewCell, containerId: Int, coordinateX:Int, coordinateY: Int, isPositive: Boolean): WidgetMap? {

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

            for (dx in 0..(widget.spanX - 1)) {
                for (dy in 0..(widget.spanY - 1)) {
                    val x = cId * widgetNumInContainerX + toX + (if (isPositive) dx else (widget.spanX - dx - 1))
                    val y = toY + dy
                    val widgetToRearrange = widgetMap[x][y] ?: continue

                    if (rearrangedWidget.contains(widgetToRearrange!!.widgetId)) continue
                    rearrangedWidget.add(widgetToRearrange.widgetId)

                    var nextX = toX + (if (isPositive) widget.spanX else -1)
                    var nextY = widgetToRearrange.positionY
                    var nextId = cId
                    while (true) {
                        if (nextX < 0) {
                            nextX = (nextX + widgetNumInContainerX) % widgetNumInContainerX
                            nextId--
                            if (nextId < 0) return null
                        }
                        if (nextX >= widgetNumInContainerX) {
                            nextX %= widgetNumInContainerX
                            nextId++
                            if (nextId >= widgetContainerNum) return null
                        }
                        if (isWidgetRearrangeable(widgetToRearrange, nextId, nextX, nextY, updatedWidgetMap)) {
                            addWidgetToMap(widgetToRearrange, nextId, nextX, nextY, updatedWidgetMap)
                            val info = WidgetRearrangeInfo(widgetToRearrange, nextId, nextX, nextY)
                            queue.push(info)
                            break
                        }
                        if (isPositive) {
                            nextX++
                        } else {
                            nextX--
                        }
                    }
                }
            }
        }
        for (x in 0..(widgetContainerNum * widgetNumInContainerX - 1)) {
            for (y in 0..(widgetNumInContainerY - 1)) {
                val widgetToReflect = widgetMap[x][y] ?: continue
                if (rearrangedWidget.contains(widgetToReflect.widgetId)) continue
                updatedWidgetMap[x][y] = widgetMap[x][y]
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