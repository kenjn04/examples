package com.example.hmi.myapplication2.temp

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.view.MotionEvent
import android.view.View
import com.example.hmi.myapplication2.Launcher
import com.example.hmi.myapplication2.common.WidgetRelocateInfo
import com.example.hmi.myapplication2.util.DraggingHelper
import java.lang.Math.abs

typealias WidgetMap = Array<Array<WidgetFrame?>>

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

    private val widgetMap: WidgetMap

    private val scale: Float
        get() = scaleX

    var widgetContainers: MutableList<WidgetContainerView> = mutableListOf()

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private val widgetContainerNum = launcher.params.widgetContainerNum
    private val widgetNumInContainerX = launcher.params.widgetNumInContainerX
    private val widgetNumInContainerY = launcher.params.widgetNumInContainerX
    private val totalX = widgetContainerNum * widgetNumInContainerX
    private val totalY = widgetNumInContainerY

    init {
        // TODO:
        relativeTranslationX = -1.0F * displaySize.x // workspace.shiftX - shiftX

        widgetMap = Array(totalX, {arrayOfNulls<WidgetFrame>(totalY)})
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 1..(widgetContainerNum)) {
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
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val absoluteX = containerId * widgetNumInContainerX + x + dx
                val absoluteY = y + dy
                if (widget.appWidgetId == 4) {
                }
                widgetMap[absoluteX][absoluteY] = widget
            }
        }
        widgetContainers[containerId].addWidget(widget, x, y)
    }

    private var engine: WidgetRelocateEngine? = null
    fun startWidgetDragging(widget: WidgetFrame) {
        draggingWidget = widget
        val container = widgetContainers[currentMainContainer]
        container.startWidgetDrag(widget)
        draggingWidgetPosition = container.calculateDraggingWidgetPosition()
        draggingWidgetOriginalContainerId = currentMainContainer

        engine = WidgetRelocateEngine(
            widgetMap,
            widgetContainerNum,
            widgetNumInContainerX,
            widgetNumInContainerY
        )
    }

    fun finishWidgetDragging() {
        updateWidgetList()
        draggingWidget = null
        widgetContainers[currentMainContainer].finishWidgetDrag(true)

        temporalRearrangedWidgets = mutableListOf()
    }

    private fun updateWidgetList() {
        for (i in 1..totalX) {
            for (j in 1..totalY) {
                val widget = widgetMap[i - 1][j - 1] ?: continue
                var match = false
                for (info in temporalRearrangedWidgets) {
                    if (info.widget.equals(widget)) {
                        match = true
                        break
                    }
                }
                if (match or (widget == draggingWidget!!)) widgetMap[i - 1][j - 1] = null
            }
        }
        for (info in temporalRearrangedWidgets) {
            val x = info.to.x
            val y = info.to.y
            val id = info.to.cId
            putWidget(info.widget, id, x, y, widgetMap)
        }
        putWidget(draggingWidget!!, draggingWidgetOriginalContainerId, draggingWidgetPosition.first, draggingWidgetPosition.second, widgetMap)
    }

    fun onWidgetDragging() {
        val container = widgetContainers[currentMainContainer]

        val newDraggingWidgetPosition = container.calculateDraggingWidgetPosition()

        if (newDraggingWidgetPosition != Pair(-1, -1)) {
            if (newDraggingWidgetPosition != draggingWidgetPosition) {
                val engine = engine!!
                pendingRearrangeWidgets = mutableListOf()
                if (engine.isWidgetDroppable(
                        draggingWidget!!, currentMainContainer,
                        newDraggingWidgetPosition.first, newDraggingWidgetPosition.second))
                {
                    pendingRearrangeWidgets = engine.widgetsToRelocate!!
                    rearrangeWidget()
                    for (i in pendingRearrangeWidgets) {
                    }
                    container.showShadowFrame(newDraggingWidgetPosition.first, newDraggingWidgetPosition.second)
                    temporalRearrangedWidgets = pendingRearrangeWidgets.toMutableList()
                }
                draggingWidgetPosition = newDraggingWidgetPosition
            } else {
                container.showShadowFrame(newDraggingWidgetPosition.first, newDraggingWidgetPosition.second)
            }
        }
    }

    private var pendingRearrangeWidgets = mutableListOf<WidgetRelocateInfo>()
    private var temporalRearrangedWidgets = mutableListOf<WidgetRelocateInfo>()
    private fun rearrangeWidget() {
        for (info in temporalRearrangedWidgets) {
            val widget = info.widget
            widgetContainers[info.to.cId].removeWidget(widget)
            widgetContainers[info.from.cId].addWidget(widget, info.from.x, info.from.y)
        }
        for (info in pendingRearrangeWidgets) {
            val widget = info.widget
            widgetContainers[info.from.cId].removeWidget(widget)
            widgetContainers[info.to.cId].addWidget(widget, info.to.x, info.to.y)
        }
    }

    private fun putWidget(widget: WidgetFrame, containerId: Int, x: Int, y: Int, map: WidgetMap) {
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val absoluteX = containerId * widgetNumInContainerX + x + dx
                val absoluteY = y + dy
                map[absoluteX][absoluteY] = widget
            }
        }
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