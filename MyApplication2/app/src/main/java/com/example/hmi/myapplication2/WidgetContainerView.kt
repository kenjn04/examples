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

    /** */
    private val widgetList = mutableListOf<WidgetFrame?>()

    private var draggingWidget: WidgetFrame? = null

    private val shadowFrame: FrameLayout = FrameLayout(context)

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

        initializeWidgetList()
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

    private fun addWidgetToList(widget: WidgetFrame, x: Int, y: Int): MutableList<WidgetFrame> {
        val replacedWidgetList = mutableListOf<WidgetFrame>()
        for (dx in 0..(widget.spanX - 1)) {
            for (dy in 0..(widget.spanY - 1)) {
                val pos = (y + dy) * numX + x + dx
                if (widgetList[pos] != null) {
                    replacedWidgetList.add(widgetList[pos]!!)
                }
                widgetList[pos] = widget
            }
        }
        return replacedWidgetList
    }

    private fun removeWidgetFromList(widget: WidgetFrame) {
        for (x in 0..(numX - 1)) {
            for (y in 0..(numY - 1)) {
                val pos = y * numX + x
                if (widgetList[pos] == widget) {
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

        val shiftX =
            (this.params.displaySize.x - this.params.widgetNumInContainerX * this.params.widgetFrameWidth).toFloat() / 2
        val shiftY =
            (this.params.displaySize.y - this.params.widgetNumInContainerY * this.params.widgetFrameHeight).toFloat() / 2
        widget.translationX = x * widgetFrameWidth + shiftX
        widget.translationY = y * widgetFrameHeight + shiftY

        widget.widgetContainerView = this

        addWidgetToList(widget, x, y)
        addView(widget)
    }

    private fun enableShadowFrame() {

        shadowFrame.layoutParams.apply {
            width = draggingWidget!!.spanX * widgetFrameWidth
            height = draggingWidget!!.spanY * widgetFrameHeight
        }
        shadowFrame.visibility = View.VISIBLE
    }

    fun moveShadowFrame() {

        if (shadowFrame.visibility != View.VISIBLE) {
            enableShadowFrame()
        }

        val shiftX =
            (this.params.displaySize.x - this.params.widgetNumInContainerX * this.params.widgetFrameWidth).toFloat() / 2
        val shiftY =
            (this.params.displaySize.y - this.params.widgetNumInContainerY * this.params.widgetFrameHeight).toFloat() / 2

        val widget = draggingWidget!!
        val positionLeft = widget.translationX - relativeTranslationX
        val positionRight = positionLeft + widgetFrameWidth * widget.spanX
        val positionTop = widget.translationY
        val positionBottom = positionTop + widgetFrameHeight * widget.spanY

        var toX: Int = -1
        var toY: Int = -1
        var maxSize: Float = 0F
        for (x in 0..(numX - widget.spanX)) {
            for (y in 0..(numY - widget.spanY)) {
                val shadowLeft = x * widgetFrameWidth + shiftX
                val shadowRight = shadowLeft + widgetFrameWidth * widget.spanX
                val shadowTop = y * widgetFrameHeight + shiftY
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
                    toX = x
                    toY = y
                }
            }
        }
        if (maxSize == 0F) {
            return
        }
        shadowFrame.apply {
            translationX = (toX * widgetFrameWidth + shiftX).toFloat()
            translationY = (toY * widgetFrameHeight + shiftY).toFloat()
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
            addWidget(draggingWidget, 0, 1)
        }

//        return rearrangeWidgetIfRequired(draggingWidget, true)
    }

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
                widgetRearrangeAnimators.add(createWidgetRearrangeAnimator(widget, x, y))
            }

            removeWidgetFromList(widget)
            val replacedWidgetList = addWidgetToList(widget, x, y)
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

    private fun createWidgetRearrangeAnimator(widget: WidgetFrame, x: Int, y: Int): ObjectAnimator {

        val toX = x * widgetFrameWidth.toFloat()
        val toY = y * widgetFrameHeight.toFloat()

        var holderX = PropertyValuesHolder.ofFloat("translationX", widget.translationX, toX)
        var holderY = PropertyValuesHolder.ofFloat("translationY", widget.translationY, toY)
        var objectAnimator = ObjectAnimator.ofPropertyValuesHolder(widget, holderX, holderY)
        objectAnimator.duration = WIDGET_REARRANGE_ANIMATION_DURATION_MS

        return objectAnimator
    }
}