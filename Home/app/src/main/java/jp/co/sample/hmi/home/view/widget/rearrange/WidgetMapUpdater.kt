package jp.co.sample.hmi.home.view.widget.rearrange

import android.util.Log
import jp.co.sample.hmi.home.util.Queue
import jp.co.sample.hmi.home.view.widget.WidgetMap
import jp.co.sample.hmi.home.view.widget.WidgetViewCell
import java.text.FieldPosition
import kotlin.math.abs

class WidgetMapUpdater(
        private val initialWidgetMap: WidgetMap,
        private val widgetContainerNum: Int,
        private val widgetNumInContainerX: Int,
        private val widgetNumInContainerY: Int
) {

    private val TAG = "WidgetMapUpdater"

    private val totalX = widgetContainerNum * widgetNumInContainerX
    private val totalY = widgetNumInContainerY

    data class WidgetRearrangeInfo(val widget: WidgetViewCell, val containerId: Int, val coordinateX: Int, val coordinateY:Int)

    fun updateWidgetMap(widget: WidgetViewCell, containerId: Int, coordinateX: Int, coordinateY: Int): WidgetMap? {

        /** copy the initial WidgetMap */
        var latestMap = copyMap(initialWidgetMap)

        /** remove widget to be moved from latestMap */
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val absoluteX = widget.containerId * widgetNumInContainerX + widget.positionX + dx
                val absoluteY = widget.positionY + dy
                latestMap[absoluteX][absoluteY] = null
            }
        }

        /** First, try to move widget to left as much as possible */
        val moveLeftSuccessWidgets = mutableSetOf<Int>()

        val containerIdToMove = if (coordinateX != 0) containerId else containerId - 1
        if (containerIdToMove >= 0) {

            val canBeMovedLeftY = mutableListOf<Boolean>()
            for (i in 0..(widget.spanY - 1)) canBeMovedLeftY.add(true)

            val rearrangedWidget = mutableSetOf<Int>()
            for (dx in 0..(widget.spanX - 1)) {
                for (dy in 0..(widget.spanY - 1)) {
                    if (!canBeMovedLeftY[dy]) continue
                    val x = coordinateX + dx
                    val y = coordinateY + dy
                    val absoluteX = containerId * widgetNumInContainerX + x
                    val absoluteY = y
                    val widgetToRearrange = initialWidgetMap[absoluteX][absoluteY] ?: continue

                    if (rearrangedWidget.contains(widgetToRearrange.widgetId)) continue
                    rearrangedWidget.add(widgetToRearrange.widgetId)

                    /** if the left edge is out of scope of widget new location, skip to move left */
                    if ((widgetToRearrange.positionX + widgetToRearrange.spanX) > (widget.positionX + widget.spanX)) {
                        canBeMovedLeftY[dy] = false
                        continue
                    }

                    /** move widget and update latest Map */
                    val newMap = calculateWidgetRearrange(
                            latestMap,
                            WidgetRearrangeInfo(widgetToRearrange, containerId, x, y),
                            false,
                            WidgetRearrangeInfo(widget, containerId, coordinateX, coordinateY)
                        )

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
        return calculateWidgetRearrange(
                latestMap,
                WidgetRearrangeInfo(widget, containerId, coordinateX, coordinateY),
                true
        )
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

    private fun calculateWidgetRearrange(
            widgetMap: WidgetMap, widgetToRearrange: WidgetRearrangeInfo, isPositive: Boolean, fixedWidgetInfo: WidgetRearrangeInfo? = null
    ): WidgetMap? {

        /** create new widgetMap to update */
        val updatedWidgetMap = Array(totalX, {arrayOfNulls<WidgetViewCell>(totalY)})
        if (fixedWidgetInfo != null) {
            val widget = fixedWidgetInfo.widget
            val containerId = fixedWidgetInfo.containerId
            val coordinateX = fixedWidgetInfo.coordinateX
            val coordinateY = fixedWidgetInfo.coordinateY
            for (dx in 0..(widget.spanX - 1)) {
                for (dy in 0..(widget.spanY - 1)) {
                    val absoluteX = containerId * widgetNumInContainerX + coordinateX + dx
                    val absoluteY = coordinateY + dy
                    updatedWidgetMap[absoluteX][absoluteY] = widget
                }
            }

        }

        /** In order NOT to move same widget several times */
        val rearrangeWidget = mutableSetOf<Int>()
        rearrangeWidget.add(widgetToRearrange.widget.widgetId)

        /** Move widget */
        val queue = Queue<WidgetRearrangeInfo>()
        queue.push(widgetToRearrange)
        while (!queue.isEmpty()) {
            val widget = queue.peek().widget
            val cId = queue.peek().containerId
            val toX = queue.peek().coordinateX
            val toY = queue.peek().coordinateY
            queue.pop()

            if ((cId < 0) or (widgetContainerNum <= cId)) return null

            var nx = toX + (if (isPositive) 1 else -1)
            var nId = cId
            if ((nx < 0) or (widgetNumInContainerX <= nx)) {
                nx = (nx + widgetNumInContainerX) % widgetNumInContainerX
                if (nx < 0) nId-- else nId++
            }

            if (isWidgetRearrangeable(widget, cId, toX, toY, updatedWidgetMap)) {
                addWidgetToMap(widget, cId, toX, toY, updatedWidgetMap)
            } else {
                queue.push(WidgetRearrangeInfo(widget, nId, nx, toY))
            }

            for (dx in 0..(widget.spanX - 1)) {
                for (dy in 0..(widget.spanY - 1)) {
                    val x = cId * widgetNumInContainerX + toX + (if (isPositive) dx else (widget.spanX - dx - 1))
                    val y = toY + dy
                    Log.d(TAG, "" + toY + " " + dy)
                    val widgetToRearrange = widgetMap[x][y] ?: continue

                    if (rearrangeWidget.contains(widgetToRearrange.widgetId)) continue
                    rearrangeWidget.add(widgetToRearrange.widgetId)

                    queue.push(WidgetRearrangeInfo(widgetToRearrange, nId, nx, y))
                }
            }
        }

        /** Update with not moved widget information */
        for (x in 0..(widgetContainerNum * widgetNumInContainerX - 1)) {
            for (y in 0..(widgetNumInContainerY - 1)) {
                val widgetToReflect = widgetMap[x][y] ?: continue
                if (rearrangeWidget.contains(widgetToReflect.widgetId)) continue
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