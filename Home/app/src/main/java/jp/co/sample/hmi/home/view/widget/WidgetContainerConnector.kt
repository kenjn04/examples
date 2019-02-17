package jp.co.sample.hmi.home.view.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.media.MicrophoneInfo
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import jp.co.sample.hmi.home.R
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.common.WidgetIdProvider
import jp.co.sample.hmi.home.repository.db.WidgetItemInfo
import jp.co.sample.hmi.home.util.DraggingHelper
import jp.co.sample.hmi.home.view.HomeActivity
import jp.co.sample.hmi.home.view.HomeMode
import jp.co.sample.hmi.home.view.HomeModeChangeListener
import jp.co.sample.hmi.home.view.widget.rearrange.WidgetMapUpdater
import java.lang.Math.abs

typealias WidgetMap = Array<Array<WidgetViewCell?>>

class WidgetContainerConnector(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle), HomeModeChangeListener, Animator.AnimatorListener {

    private val home = context as HomeActivity

    private val displaySize: Point = home.params.displaySize
    private val shiftX: Float = 1.5F * displaySize.x
    val relativeTranslationX: Float

    private var currentMainContainer = 2

    private var duringTransition = false

    val widgetAddCell: WidgetAddCell

    private val widgetViewCellMap = mutableMapOf<Int, WidgetViewCell>()

    private var draggingWidget: WidgetViewCell? = null
    private var draggingWidgetOriginalContainerId: Int = -1

    private var draggingWidgetPosition: Pair<Int, Int>? = null

    private val draggingHelper = DraggingHelper(this, false)

    private var widgetMap: WidgetMap

    private val scale: Float
        get() = scaleX

    private var widgetContainers: MutableList<WidgetContainerView> = mutableListOf()

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private val widgetContainerNum = home.params.widgetContainerNum
    private val widgetNumInContainerX = home.params.widgetNumInContainerX
    private val widgetNumInContainerY = home.params.widgetNumInContainerY
    private val totalX = widgetContainerNum * widgetNumInContainerX
    private val totalY = widgetNumInContainerY

    init {
        // TODO:
        relativeTranslationX = -1.0F * displaySize.x // shrinkTable.shiftX - shiftX

        widgetMap = Array(totalX, {arrayOfNulls<WidgetViewCell>(totalY)})

        widgetAddCell = home.layoutInflater.inflate(R.layout.widget_add_cell, this, false) as WidgetAddCell
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 1..(widgetContainerNum)) {
            val widgetContainer = WidgetContainerView(home, i - 1)
            val layoutParam = FrameLayout.LayoutParams(
                displaySize.x - 20, displaySize.y
            ).apply {
                gravity = Gravity.TOP or Gravity.LEFT
            }
            widgetContainer.layoutParams = layoutParam
            widgetContainer.setBackgroundColor(Color.GRAY)
            widgetContainers.add(widgetContainer)
        }
        initializeWidgetContainers()
        widgetContainers[0].addWidget(widgetAddCell, 0, 0)
    }

    private fun initializeWidgetContainers() {
        for (container in widgetContainers) {
            addView(container)
        }
        reLayoutWidgetContainer()
    }

    private fun reLayoutWidgetContainer() {
        translationX = relativeTranslationX
        for (container in widgetContainers) {
            container.visibility = View.GONE
            container.translationX = 0F
            container.relativeTranslationX = 0F
        }
        for (i in -2..2) {
            val j = (currentMainContainer + i + widgetContainers.size) % widgetContainers.size
            val container = widgetContainers[j]
            container.apply {
                translationX = shiftX + i * displaySize.x
                visibility = View.VISIBLE
            }
            container.initialize(this)
        }
    }

    fun addWidget(widget: WidgetViewCell) {
        val containerId = widget.item.containerId
        val x = widget.item.coordinateX
        val y = widget.item.coordinateY
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val absoluteX = containerId * widgetNumInContainerX + x + dx
                val absoluteY = y + dy
                widgetMap[absoluteX][absoluteY] = widget
            }
        }
        widgetViewCellMap[widget.item.id] = widget
        widgetContainers[containerId].addWidget(widget, x, y)
        reLayoutWidgetAddView()
    }

    fun deleteWidget(item: WidgetItemInfo) {
        val widget = widgetViewCellMap[item.id]!!
        val containerId = widget.item.containerId
        val x = widget.item.coordinateX
        val y = widget.item.coordinateY
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val absoluteX = containerId * widgetNumInContainerX + x + dx
                val absoluteY = y + dy
                widgetMap[absoluteX][absoluteY] = null
            }
        }
        widget.deleteFromContainer()
    }

    fun updateWidget(pItem: WidgetItemInfo, lItem: WidgetItemInfo) {
        val widget = widgetViewCellMap[pItem.id]!!
        widget.deleteFromContainer()
        widget.item = lItem
        addWidget(widget)
        widgetAddCell.deleteFromContainer()
    }

    private fun reLayoutWidgetAddView() {
        widgetAddCell.deleteFromContainer()
        for (id in (widgetContainerNum - 1) downTo 0) {
            for (x in (widgetNumInContainerX - 1) downTo 0 ) {
                for (y in (widgetNumInContainerY - 1) downTo 0 ) {
                    val absoluteX = id * widgetNumInContainerX + x
                    val absoluteY = y
                    if (widgetMap[absoluteX][absoluteY] != null) {
                        var id2 = id
                        var x2 = x
                        var y2 = y
                        y2++
                        if (y2 == widgetNumInContainerY) {
                            y2 = 0
                            x2++
                        }
                        if (x2 == widgetNumInContainerX) {
                            x2 = 0
                            id2++
                        }

                        if (id2 == widgetContainerNum) {
                            // There is no space for WidgetAddView
                        } else {
                            widgetContainers[id2].addWidget(widgetAddCell, x2, y2)
                        }
                        return
                    }
                }
            }
        }
        widgetContainers[0].addWidget(widgetAddCell, 0, 0)
    }

    fun rearrangeWidgets() {
    }

    fun createAddItem(pInfo: HomeAppWidgetProviderInfo): WidgetItemInfo? {
        var id = widgetAddCell.item.containerId
        var x = widgetAddCell.item.coordinateX
        var y = widgetAddCell.item.coordinateY

        val spanX = pInfo.spanX
        val spanY = pInfo.spanY
        while (true) {
            if (isAddable(id, x, y, spanX, spanY)) {
                return WidgetItemInfo(
                    pInfo.provider.packageName, pInfo.provider.className, id, x, y, 1, 1
                )
            }
            y++
            if (y == widgetNumInContainerY) {
                x++
                y = 0
            }
            if (x == widgetNumInContainerX) {
                x = 0
                id++
            }
            if (id == widgetContainerNum) break
        }
        return null
    }

    fun isAddable(containerId: Int, coordinateX: Int, coordinateY: Int, spanX: Int, spanY: Int): Boolean {
        for (dx in 0..(spanX - 1)) {
            for (dy in 0..(spanY - 1)) {
                if ((coordinateX + dx) >= widgetNumInContainerX) return false
                if ((coordinateY + dy) >= widgetNumInContainerY) return false
                val x = containerId * widgetNumInContainerX + coordinateX + dx
                val y = coordinateY + dy
                if (widgetMap[x][y] != null) return false
            }
        }
        return true
    }

    override fun onHomeModeChanged(mode: HomeMode) {
        when (mode) {
            HomeMode.DISPLAY -> {
                reLayoutWidgetAddView()
            }
            HomeMode.REARRANGEMENT -> {
                widgetAddCell.deleteFromContainer()
            }
            HomeMode.SELECTION -> {
                // Nothing to do
            }
        }
    }

    private var updater: WidgetMapUpdater? = null

    fun startWidgetDragging(widget: WidgetViewCell) {
        draggingWidget = widget
        val container = widgetContainers[currentMainContainer]
        container.startWidgetDrag(widget)

        draggingWidgetPosition = container.calculateDraggingWidgetPosition()
        draggingWidgetOriginalContainerId = currentMainContainer

        updater = WidgetMapUpdater(
                widgetMap,
                widgetContainerNum,
                widgetNumInContainerX,
                widgetNumInContainerY
        )
    }

    fun finishWidgetDragging() {
        val container = widgetContainers[currentMainContainer]
        val newDraggingWidgetPosition = container.calculateDraggingWidgetPosition()
        if (newDraggingWidgetPosition != null) {
            val updatedWidgetMap = updater!!.updateWidgetMap(
                draggingWidget!!, currentMainContainer, newDraggingWidgetPosition.first, newDraggingWidgetPosition.second
            )

            home.shrinkTable.removeView(draggingWidget)
            draggingWidget!!.revertPosition()
            if (updatedWidgetMap != null) {
                val items = getItemsFromWidgetMap(updatedWidgetMap)
                home.updateWidget(items)
                widgetMap = updatedWidgetMap
            }
        } else {
            // Revert
            val widget = draggingWidget!!
            home.shrinkTable.removeView(widget)
            val id = widget.item.containerId
            val x = widget.item.coordinateX
            val y = widget.item.coordinateY
            widgetContainers[id].addWidget(widget, x, y)
        }

        draggingWidget = null
        widgetContainers[currentMainContainer].finishWidgetDrag(true)
    }

    fun onWidgetDragging() {
        val container = widgetContainers[currentMainContainer]
        val newDraggingWidgetPosition = container.calculateDraggingWidgetPosition()

        container.disableShadowFrame()
        if (newDraggingWidgetPosition != null) {
            if (newDraggingWidgetPosition != draggingWidgetPosition) {
                container.showShadowFrame(newDraggingWidgetPosition.first, newDraggingWidgetPosition.second)
                draggingWidgetPosition = newDraggingWidgetPosition
            } else {
                container.showShadowFrame(newDraggingWidgetPosition.first, newDraggingWidgetPosition.second)
            }
        }
    }

    private fun getItemsFromWidgetMap(map: WidgetMap): List<WidgetItemInfo> {
        val itemSet = mutableSetOf<Int>()
        val updatedItems = mutableListOf<WidgetItemInfo>()
        for (x in 0..(totalX - 1)) {
            for (y in 0..(totalY - 1)) {
                val widget = map[x][y] ?: continue
                val item = widget.item
                if (!itemSet.contains(item.id)) {
                    itemSet.add(item.id)
                    val updatedItem = WidgetItemInfo(item).apply {
                        containerId = x / widgetNumInContainerX
                        coordinateX = x % widgetNumInContainerX
                        coordinateY = y
                    }
                    if (updatedItem != item) {
                        updatedItems.add(updatedItem)
                    }
                }
            }
        }
        return updatedItems.toList()
    }

    private fun transitContainerIfRequired() {
        val translationDiffX = translationX - relativeTranslationX
        if (abs(translationDiffX) < (displaySize.x.toFloat() * 0.5 * scale)) {
            return
        }
        if (translationDiffX < 0) {
            transitContainer(false, null)
        } else {
            transitContainer(true, null)
        }
    }

    fun transitContainer(right: Boolean, velocity: Float?) {
        if (duringTransition) return
        if (draggingWidget != null) {
            widgetContainers[currentMainContainer].finishWidgetDrag(false)
        }

        if (right) {
            duringTransition = true
            createTransitContainerAnimator(displaySize.x * scale + relativeTranslationX, velocity).start()
            currentMainContainer = (currentMainContainer + widgetContainers.size - 1) % widgetContainers.size
        } else {
            duringTransition = true
            createTransitContainerAnimator(-(displaySize.x * scale - relativeTranslationX), velocity).start()
            currentMainContainer = (currentMainContainer + 1) % widgetContainers.size
        }

        if (draggingWidget != null) {
            widgetContainers[currentMainContainer].startWidgetDrag(draggingWidget!!)
        }
    }

    private fun createTransitContainerAnimator(toX: Float, velocity: Float?): ObjectAnimator {

        var holderX = PropertyValuesHolder.ofFloat("translationX", translationX, toX)
        var objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, holderX)
        if (velocity != null) {
            objectAnimator.duration = (abs(translationX - toX) / velocity).toLong()
        } else {
            objectAnimator.duration = 1000
        }
        objectAnimator.addListener(this)

        return objectAnimator
    }

    override fun onAnimationStart(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
        reLayoutWidgetContainer()
        duringTransition = false
    }

    override fun onAnimationCancel(animation: Animator?) {}
    override fun onAnimationRepeat(animation: Animator?) {}

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_MOVE -> {
                if (draggingWidget == null) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!duringTransition) transitContainerIfRequired()
                if (!draggingHelper.dragStarted) {
                    return draggingHelper.startDragging(event.x, event.y)
                } else {
                    draggingHelper.movePositionByDrag(event.x, event.y)
                }
            }
            MotionEvent.ACTION_DOWN -> {
                return draggingHelper.startDragging(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                draggingHelper.finishDragging(true)
            }
            MotionEvent.ACTION_CANCEL -> {
                draggingHelper.finishDragging(false)
            }
        }
        return super.onTouchEvent(event)
    }
}