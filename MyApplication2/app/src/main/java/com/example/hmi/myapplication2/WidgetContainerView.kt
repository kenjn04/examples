package com.example.hmi.myapplication2

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.view.Gravity
import android.view.MotionEvent

class WidgetContainerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    private val WIDGET_REARRANGE_ANIMATION_DURATION_MS = 500L

    private val widgetFrameWidth: Int = resources.getDimensionPixelSize(R.dimen.widget_frame_width)
    private val widgetFrameHeight: Int = resources.getDimensionPixelSize(R.dimen.widget_frame_height)

    private val numX: Int = 4
    private val numY: Int = 2

    private val widgetList = mutableListOf<WidgetFrame?>()

    private var draggingWidget: WidgetFrame? = null

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        initializeWidgetList()
    }

    private fun initializeWidgetList() {
        for (x in 0..(numX - 1)) {
            for (y in 0..(numY - 1)) widgetList.add(null)
        }
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

        /** Layout is managed by translationX and translationY because of animation */
        widget.translationX = (x * widgetFrameWidth).toFloat()
        widget.translationY = (y * widgetFrameHeight).toFloat()

        addWidgetToList(widget, x, y)
        addView(widget)
    }

    fun startWidgetDrag(widget: WidgetFrame) {
        draggingWidget = widget
    }

    fun endWidgetDrag(): Boolean {

        val draggingWidget = draggingWidget!!
        this.draggingWidget = null

        return rearrangeWidgetIfRequired(draggingWidget, true)
    }

    private fun rearrangeWidgetIfRequired(draggingWidget: WidgetFrame, includeDragWidget: Boolean): Boolean {

        var positionX = draggingWidget.translationX + (draggingWidget.spanX * widgetFrameWidth) / 2
        var positionY = draggingWidget.translationY + (draggingWidget.spanY * widgetFrameHeight) / 2
        positionX = minOf(maxOf(positionX, 0F), (widgetFrameWidth * numX - 1).toFloat())
        positionY = minOf(maxOf(positionY, 0F), (widgetFrameHeight * numY - 1).toFloat())

        if (!isWidgetRearrangeRequired(draggingWidget, positionX, positionY)) {
            // Nothing to do
            return true
        }

        val toX = (positionX / widgetFrameWidth).toInt()
        val toY = (positionY / widgetFrameHeight).toInt()

        return rearrangeWidgets(draggingWidget, toX, toY, includeDragWidget)
    }

    private fun isWidgetRearrangeRequired(widget: WidgetFrame, positionX: Float, positionY: Float): Boolean {
        return true
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
        Log.d("zzzzzz", widgetRearrangeAnimators.size.toString())

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

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_MOVE -> {
                rearrangeWidgetIfRequired(draggingWidget!!, false)
            }
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_UP -> {
            }
            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}