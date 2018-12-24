package com.example.hmi.myapplication2

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.view.MotionEvent
import android.view.View
import com.example.hmi.myapplication2.util.DraggingHelper
import com.example.hmi.myapplication2.util.Queue
import java.lang.Math.abs

class WidgetContainerConnector(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle), Animator.AnimatorListener {

    private val launcher = context as Launcher

    private val displaySize: Point = launcher.params.displaySize
    private val shiftX: Float = 1.5F * displaySize.x
    val relativeTranslationX: Float

    private var currentMainContainer = 0

    private var duringTransition = false

    private var draggingWidget: WidgetFrame? = null
    private var draggingWidgetOriginalContainerId: Int = -1

    private var draggingWidgetPosition: Pair<Int, Int> = Pair(-1, -1)

    private val draggingHelper = DraggingHelper(this, false)

    private val widgetMap = mutableMapOf<Int, WidgetFrame>()

    private val widgetArray: Array<Array<Int>>
    private var pendingWidgetArray: Array<Array<Int>>

    private val scale: Float
        get() = scaleX

    var widgetContainers: MutableList<WidgetContainerView> = mutableListOf()

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        // TODO:
        relativeTranslationX = -1.0F * displaySize.x // workspace.shiftX - shiftX

        val totalX = launcher.params.widgetContainerNum * launcher.params.widgetNumInContainerX
        val totalY = launcher.params.widgetNumInContainerY
        widgetArray = Array(totalX, { Array(totalY, { 0 }) })
        pendingWidgetArray = Array(totalX, { Array(totalY, { 0 }) })
    }

    fun showWidgetArray() {
        val totalX = launcher.params.widgetNumInContainerX // launcher.params.widgetContainerNum * launcher.params.widgetNumInContainerX
        val totalY = launcher.params.widgetNumInContainerY
        for (i in 1..totalX) {
            for (j in 1..totalY) {
                Log.d("abcde", "" + (i - 1) + " " + (j - 1) + " " + widgetArray[i - 1][j - 1])
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 1..(launcher.params.widgetContainerNum)) {
            val widgetContainer = WidgetContainerView(launcher)
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
            val k = i + 1
            val container = widgetContainers[j]
            container.apply {
                translationX = shiftX + i * displaySize.x
                visibility = View.VISIBLE
            }
            container.initialize(this)
        }
    }

    fun addWidget(widget: WidgetFrame, containerId: Int, x: Int, y: Int) {
        widgetMap.put(widget.appWidgetId, widget)
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val absoluteX = containerId * launcher.params.widgetNumInContainerX + x + dx
                val absoluteY = y + dy
                if (widget.appWidgetId == 4) {
                }
                widgetArray[absoluteX][absoluteY] = widget.appWidgetId
            }
        }
        widgetContainers[containerId].addWidget(widget, x, y)
    }

    fun startWidgetDragging(widget: WidgetFrame) {
        draggingWidget = widget
        val container = widgetContainers[currentMainContainer]
        container.startWidgetDrag(widget)
        draggingWidgetPosition = container.calculateDraggingWidgetPosition()
        draggingWidgetOriginalContainerId = currentMainContainer
        showWidgetArray()
    }

    fun finishWidgetDragging() {
        updateWidgetList()
        draggingWidget = null
        widgetContainers[currentMainContainer].finishWidgetDrag(true)

        temporalRearrangedWidgets = mutableListOf()
    }

    private fun updateWidgetList() {
        val totalX = launcher.params.widgetContainerNum * launcher.params.widgetNumInContainerX
        val totalY = launcher.params.widgetNumInContainerY
        for (i in 1..totalX) {
            for (j in 1..totalY) {
                val id = widgetArray[i - 1][j - 1]
                var match = false
                for (info in temporalRearrangedWidgets) {
                    if (info.widget.appWidgetId == id) {
                        match = true
                        break
                    }
                }
                if (match or (id == draggingWidget!!.appWidgetId)) widgetArray[i - 1][j - 1] = 0
            }
        }
        for (info in temporalRearrangedWidgets) {
            val x = info.toX
            val y = info.toY
            val id = info.toContainerId
            putWidget(info.widget, id, x, y, widgetArray)
        }
        putWidget(draggingWidget!!, draggingWidgetOriginalContainerId, draggingWidgetPosition.first, draggingWidgetPosition.second, widgetArray)
        showWidgetArray()
    }

    fun onWidgetDragging() {
        val container = widgetContainers[currentMainContainer]

        val newDraggingWidgetPosition = container.calculateDraggingWidgetPosition()

        if (newDraggingWidgetPosition != Pair(-1, -1)) {
            if (newDraggingWidgetPosition != draggingWidgetPosition) {
                val totalX = launcher.params.widgetContainerNum * launcher.params.widgetNumInContainerX
                val totalY = launcher.params.widgetNumInContainerY
                pendingWidgetArray = Array(totalX, { Array(totalY, { 0 }) })

                pendingRearrangeWidgets = mutableListOf()
                if (calculateRearrangeWidget(
                        draggingWidget!!,
                        newDraggingWidgetPosition.first,
                        newDraggingWidgetPosition.second
                    )
                ) {
                    rearrangeWidget()
                    container.showShadowFrame(newDraggingWidgetPosition.first, newDraggingWidgetPosition.second)
                    temporalRearrangedWidgets = pendingRearrangeWidgets.toMutableList()
                }
                draggingWidgetPosition = newDraggingWidgetPosition
            } else {
                container.showShadowFrame(newDraggingWidgetPosition.first, newDraggingWidgetPosition.second)
            }
        }
    }

    private var pendingRearrangeWidgets = mutableListOf<WidgetRearrangeInfo>()
    private var temporalRearrangedWidgets = mutableListOf<WidgetRearrangeInfo>()
    private fun rearrangeWidget() {
        for (info in temporalRearrangedWidgets) {
            val widget = info.widget
            widgetContainers[info.toContainerId].removeWidget(widget)
            widgetContainers[info.fromContainerId].addWidget(widget, info.fromX, info.fromY)
        }
        for (info in pendingRearrangeWidgets) {
            val widget = info.widget
            widgetContainers[info.fromContainerId].removeWidget(widget)
            widgetContainers[info.toContainerId].addWidget(widget, info.toX, info.toY)
        }
    }

    data class WidgetRearrangeInfo(
        val widget: WidgetFrame, val fromContainerId: Int, val fromX: Int, val fromY: Int,
        val toContainerId: Int, val toX: Int, val toY: Int
    )

    private fun calculateRearrangeWidget(widget: WidgetFrame, toX: Int, toY: Int): Boolean {

        val rearrangeWidgetsQueue = Queue<WidgetRearrangeInfo>()
        rearrangeWidgetsQueue.push(WidgetRearrangeInfo(
            widget, draggingWidgetOriginalContainerId, -1, -1, currentMainContainer, toX, toY
        ))
//        pendingRearrangeWidgets.add(WidgetRearrangeInfo(
//            widget, draggingWidgetOriginalContainerId, currentMainContainer, toX, toY
//        ))
        putWidget(widget, currentMainContainer, toX, toY, pendingWidgetArray)

        val rearrangedWidgetId = mutableSetOf<Int>()
        rearrangedWidgetId.add(widget.appWidgetId)
        while (!rearrangeWidgetsQueue.isEmpty()) {
            val widget = rearrangeWidgetsQueue.peek().widget
            val x = rearrangeWidgetsQueue.peek().toX
            val y = rearrangeWidgetsQueue.peek().toY
            val id = rearrangeWidgetsQueue.peek().toContainerId
            rearrangeWidgetsQueue.pop()

            for (dy in 0..(widget.spanY - 1)) {
                for (dx in 0..(widget.spanX - 1)) {
                    val absoluteX = id * launcher.params.widgetNumInContainerX + x + dx
                    val absoluteY = y + dy
                    val originalWidgetId = widgetArray[absoluteX][absoluteY]
                    if (originalWidgetId == 0) continue
                    if (rearrangedWidgetId.contains(originalWidgetId)) continue
                    rearrangedWidgetId.add(originalWidgetId)

                    val originalWidget = widgetMap.get(originalWidgetId)!!
                    var nextX = x + widget.spanX
                    var nextId = id
                    while (true) {
                        if (isWidgetPuttable(originalWidget, nextId, nextX, absoluteY, pendingWidgetArray)) {
                            putWidget(originalWidget, nextId, nextX, absoluteY, pendingWidgetArray)
                            rearrangeWidgetsQueue.push(WidgetRearrangeInfo(originalWidget, id, x + dx, y + dy, nextId, nextX, absoluteY))
                            pendingRearrangeWidgets.add(WidgetRearrangeInfo(originalWidget, id, x + dx, y + dy, nextId, nextX, absoluteY))
                            break
                        }
                        nextX++
                        if (nextX >= launcher.params.widgetNumInContainerX) {
                            nextX %= launcher.params.widgetNumInContainerX
                            nextId++
                            if (nextId >= launcher.params.widgetContainerNum) return false
                        }
                    }
                }
            }
        }
        return true
    }

    private fun putWidget(widget: WidgetFrame, containerId: Int, x: Int, y: Int, array: Array<Array<Int>>) {
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val absoluteX = containerId * launcher.params.widgetNumInContainerX + x + dx
                val absoluteY = y + dy
                array[absoluteX][absoluteY] = widget.appWidgetId
            }
        }
    }

    private fun isWidgetPuttable(widget: WidgetFrame, containerId: Int, x: Int, y: Int, array: Array<Array<Int>>): Boolean {
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                if ((x + dx) >= launcher.params.widgetNumInContainerX) return false
                if ((y + dy) >= launcher.params.widgetNumInContainerY) return false
                val absoluteX = containerId * launcher.params.widgetNumInContainerX + x + dx
                val absoluteY = y + dy
                if (array[absoluteX][absoluteY] != 0) return false
            }
        }
        return true
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