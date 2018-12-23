package com.example.hmi.myapplication2

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import com.example.hmi.myapplication2.util.DraggingHelper
import com.example.hmi.myapplication2.util.Queue

class WidgetContainerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    private val launcher: Launcher = context as Launcher

    lateinit var containerConnector: WidgetContainerConnector

    private val params = launcher.params

    /** Parameters */
    private val WIDGET_REARRANGE_ANIMATION_DURATION_MS = 500L

    private val numX: Int
    private val numY: Int
    private val widgetFrameWidth: Int
    private val widgetFrameHeight: Int

    var relativeTranslationX: Float = 0F

    private lateinit var widgetPositionX: MutableList<Float>
    private lateinit var widgetPositionY: MutableList<Float>

    /** */
    private val widgetMap = mutableMapOf<Int, WidgetFrame>()

    private val widgetList = mutableListOf<Int?>()

    private var draggingWidget: WidgetFrame? = null

    private val shadowFrame: FrameLayout = FrameLayout(context)
    private var shadowX: Int = -1
    private var shadowY: Int = -1

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        // set parameters
        numX = params.widgetNumInContainerX
        numY = params.widgetNumInContainerY
        widgetFrameWidth = params.widgetFrameWidth
        widgetFrameHeight = params.widgetFrameHeight

        // set shadow frame
        shadowFrame.visibility = View.GONE
        shadowFrame.setBackgroundColor(Color.CYAN)
        addView(shadowFrame)

        initializeWidgetPosition()
        initializeWidgetList()
    }

    private fun initializeWidgetPosition() {
        var positionX = (params.displaySize.x - params.widgetNumInContainerX * params.widgetFrameWidth).toFloat() / 2
        var positionY = (params.displaySize.y - params.widgetNumInContainerY * params.widgetFrameHeight).toFloat() / 2
        widgetPositionX = mutableListOf()
        widgetPositionY = mutableListOf()
        for (x in 1..numX) {
            widgetPositionX.add(positionX)
            positionX += params.widgetFrameWidth.toFloat()
        }
        for (y in 0..(numY - 1)) {
            widgetPositionY.add(positionY)
            positionY += params.widgetFrameHeight.toFloat()
        }
    }

    private fun initializeWidgetList() {
        for (x in 0..(numX - 1)) {
            for (y in 0..(numY - 1)) widgetList.add(null)
        }
    }

    fun initialize(connector: WidgetContainerConnector) {
        relativeTranslationX = translationX + connector.relativeTranslationX
        containerConnector = connector
    }


    private fun addWidgetToList(id: Int, x: Int, y: Int): MutableList<Int> {
        val replacedWidgetList = mutableListOf<Int>()
        val widget = widgetMap.get(id)!!
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val pos = (y + dy) * numX + x + dx
                if (widgetList[pos] != null) {
                    replacedWidgetList.add(widgetList[pos]!!)
                }
                widgetList[pos] = id
            }
        }
        return replacedWidgetList
    }

    private fun removeWidgetFromList(id: Int) {
        for (x in 0..(numX - 1)) {
            for (y in 0..(numY - 1)) {
                val pos = y * numX + x
                if (widgetList[pos] == id) {
                    widgetList[pos] = null
                }
            }
        }
    }

    fun addWidget(widget: WidgetFrame, x: Int, y: Int) {

        val width = widget.spanX * widgetFrameWidth
        val height = widget.spanY * widgetFrameHeight
        val params = widget.layoutParams

        var layoutParams: FrameLayout.LayoutParams? = null
        if (params != null) {
            layoutParams = FrameLayout.LayoutParams(params).apply {
                this.width = width
                this.height = height
            }
        } else {
            layoutParams = FrameLayout.LayoutParams(width, height)
        }
        layoutParams.gravity = Gravity.TOP or Gravity.LEFT
        widget.layoutParams = layoutParams

        widget.translationX = widgetPositionX[x]
        widget.translationY = widgetPositionY[y]

        widget.widgetContainerView = this

        widgetMap.put(widget.appWidgetId, widget)
        addWidgetToList(widget.appWidgetId, x, y)
        addView(widget)
    }

    fun removeWidget(widget: WidgetFrame) {
        widgetMap.remove(widget.appWidgetId)
        removeWidgetFromList(widget.appWidgetId)
        removeView(widget)
    }

    private fun enableShadowFrame() {

        shadowFrame.layoutParams.apply {
            width = draggingWidget!!.spanX * widgetFrameWidth
            height = draggingWidget!!.spanY * widgetFrameHeight
        }
        shadowFrame.visibility = View.VISIBLE
    }

    fun onWidgetDragging() {
        moveShadowFrame()
    }

    private fun moveShadowFrame() {

        if (shadowFrame.visibility != View.VISIBLE) {
            enableShadowFrame()
        }

        val widget = draggingWidget!!
        val positionLeft = widget.translationX - relativeTranslationX
        val positionRight = positionLeft + widgetFrameWidth * widget.spanX
        val positionTop = widget.translationY
        val positionBottom = positionTop + widgetFrameHeight * widget.spanY

//        var toX: Int = -1
//        var toY: Int = -1
        var maxSize: Float = 0F
        for (x in 0..(numX - widget.spanX)) {
            for (y in 0..(numY - widget.spanY)) {
                val shadowLeft = widgetPositionX[x]
                val shadowRight = shadowLeft + widgetFrameWidth * widget.spanX
                val shadowTop = widgetPositionY[y]
                val shadowBottom = shadowTop + widgetFrameHeight * widget.spanY

                var width: Float = 0F
                var height: Float = 0F
                if ((shadowLeft <= positionLeft) and (positionLeft < shadowRight)) {
                    width = shadowRight - positionLeft
                } else if ((positionLeft <= shadowLeft) and (shadowLeft < positionRight)) {
                    width = positionRight - shadowLeft
                }
                if ((shadowTop <= positionTop) and (positionTop < shadowBottom)) {
                    height = shadowBottom - positionTop
                } else if ((positionTop <= shadowTop) and (shadowTop < positionBottom)) {
                    height = positionBottom - shadowTop
                }
                val size = width * height
                if (size > maxSize) {
                    maxSize = size
                    shadowX = x
                    shadowY = y
                }
            }
        }
        if (maxSize == 0F) {
            return
        }
        shadowFrame.apply {
            translationX = widgetPositionX[shadowX]
            translationY = widgetPositionY[shadowY]
        }
    }

    fun startWidgetDrag(widget: WidgetFrame) {
        draggingWidget = widget
//        enableShadowFrame()
    }

    //    fun finishWidgetDrag(): Boolean {
    fun finishWidgetDrag(drop: Boolean) {

        val draggingWidget = draggingWidget!!
        this.draggingWidget = null

        shadowFrame.visibility = View.GONE

        if (drop) {
            launcher.workspace.removeView(draggingWidget)
            addWidget(draggingWidget, shadowX, shadowY)
        }

//        return rearrangeWidgetIfRequired(draggingWidget, true)
    }
/*
    private fun rearrangeWidgetIfRequired(draggingWidget: WidgetFrame, includeDragWidget: Boolean): Boolean {

        val toX = (shadowFrame.translationX / widgetFrameWidth).toInt()
        val toY = (shadowFrame.translationY / widgetFrameHeight).toInt()

        return rearrangeWidgets(draggingWidget, toX, toY, includeDragWidget)
    }

    data class WidgetRearrangeInfo(val widget: WidgetFrame, val x: Int, val y: Int, val move: Boolean)

    private fun rearrangeWidgets(draggingWidget: WidgetFrame, x: Int, y: Int, includeDragWidget: Boolean): Boolean {

        val moveWidgetsQueue = Queue<WidgetRearrangeInfo>()
        moveWidgetsQueue.push(WidgetRearrangeInfo(draggingWidget, x, y, includeDragWidget))

        val widgetRearrangeAnimators = mutableListOf<Animator>()
        while (!moveWidgetsQueue.isEmpty()) {
            val widget = moveWidgetsQueue.peek().widget
            val toX = moveWidgetsQueue.peek().x
            val toY = moveWidgetsQueue.peek().y
            val move = moveWidgetsQueue.peek().move
            moveWidgetsQueue.pop()

            if (!isWidgetMovable(widget, toX, toY)) {
                return false
            }
            if (move) {
                widgetRearrangeAnimators.add(createWidgetRearrangeAnimator(widget.appWidgetId, x, y))
            }

            removeWidgetFromList(widget.appWidgetId)
            val replacedWidgetList = addWidgetToList(widget.appWidgetId, x, y)
            for (replacedWidget in replacedWidgetList) {
                // TODO: Confirm how to move the widget
                widgetRearrangeAnimators.add(createWidgetRearrangeAnimator(replacedWidget, 0, 1))
                break
            }
            break
        }

        AnimatorSet().apply {
            playTogether(widgetRearrangeAnimators)
            start()
        }

        return true
    }

    private fun isWidgetMovable(widget: WidgetFrame, x: Int, y: Int): Boolean {
        val spanX = widget.spanX
        val spanY = widget.spanY
        for (dx in 0..(spanX - 1)) {
            for (dy in 0..(spanY - 1)) {
                if ((x + dx >= numX) or (y + dy >= numY)) {
                    return false
                }
            }
        }
        return true
    }

    private fun createWidgetRearrangeAnimator(id: Int, x: Int, y: Int): ObjectAnimator {

        val widget = widgetMap.get(id)!!

        val toX = x * widgetFrameWidth.toFloat()
        val toY = y * widgetFrameHeight.toFloat()

        var holderX = PropertyValuesHolder.ofFloat("translationX", widget.translationX, toX)
        var holderY = PropertyValuesHolder.ofFloat("translationY", widget.translationY, toY)
        var objectAnimator = ObjectAnimator.ofPropertyValuesHolder(widget, holderX, holderY)
        objectAnimator.duration = WIDGET_REARRANGE_ANIMATION_DURATION_MS

        return objectAnimator
    }
*/
}