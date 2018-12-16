package com.example.hmi.myapplication2

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.AsyncTask
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.GridLayout
import android.view.Gravity
import android.view.MotionEvent
import android.view.animation.AnimationSet

class WidgetContainerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    private val WIDGET_REARRANGE_ANIMATION_DURATION_MS = 500L

    private val widgetFrameWidth: Int
    private val widgetFrameHeight: Int

    private val numX: Int = 4
    private val numY: Int = 2

    private var launcher: Launcher? = null

    private val widgetList = mutableListOf<WidgetFrame?>()

    private var draggingWidget: WidgetFrame? = null

    private var widgetRearrangeAnimators = mutableListOf<Animator>()

    private val replacedWidgetQueue = mutableListOf<WidgetFrame>()

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        if (context is Launcher) {
            launcher = context as Launcher
        }
        widgetFrameWidth = resources.getDimensionPixelSize(R.dimen.widget_frame_width)
        widgetFrameHeight = resources.getDimensionPixelSize(R.dimen.widget_frame_height)

        // Initialize widget List
        for (x in 0..(numX - 1)) {
            for (y in 0..(numY - 1)) widgetList.add(null)
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
        /*
        layoutParams.leftMargin = x * widgetFrameWidth
        layoutParams.topMargin = y * widgetFrameHeight
        */
        widget.translationX = (x * widgetFrameWidth).toFloat()
        widget.translationY = (y * widgetFrameHeight).toFloat()

        addWidgetToList(widget, x, y)
        addView(widget)
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

    fun startWidgetDrag(widget: WidgetFrame) {
        draggingWidget = widget
    }

    fun endWidgetDrag(): Boolean {

        val draggingWidget = draggingWidget!!
        val positionX = (draggingWidget.translationX + (draggingWidget.spanX * widgetFrameWidth) / 2).toInt()
        val positionY = (draggingWidget.translationY + (draggingWidget.spanY * widgetFrameHeight) / 2).toInt()

        if (moveWidget(draggingWidget!!, positionX / widgetFrameWidth, positionY / widgetFrameHeight)) {
            commitWidgetRearrange()
        }
        this.draggingWidget = null

        // TODO: Is there any case to return false?
        return true
    }

    // TODO: Confirm how to move the widget
    // TODO: dicstra?
    private fun moveWidget(widget: WidgetFrame, x: Int, y: Int): Boolean {

        removeWidgetFromList(widget)
        replacedWidgetQueue.addAll(addWidgetToList(widget, x, y))

        val toX = x * widgetFrameWidth.toFloat()
        val toY = y * widgetFrameHeight.toFloat()

        var holderX = PropertyValuesHolder.ofFloat("translationX", widget.translationX, toX)
        var holderY = PropertyValuesHolder.ofFloat("translationY", widget.translationY, toY)
        var objectAnimator = ObjectAnimator.ofPropertyValuesHolder(widget, holderX, holderY)
        objectAnimator.setDuration(WIDGET_REARRANGE_ANIMATION_DURATION_MS)
        objectAnimator.start()
        widgetRearrangeAnimators.add(objectAnimator)

        /*
        for (replacedWidget in replacedWidgetQueue) {
            var position = Position(-1, -1)
            if (searchNewWidgetPosition(replacedWidget, position)) {
                moveWidget(replacedWidget, 0, 1)
            } else {
                return false
            }
        }
        */
        return true
    }

    // TODO: need to be reconsidered
    data class Position(val x: Int, val y: Int)

    private fun searchNewWidgetPosition(widget: WidgetFrame, position: Position): Boolean {
        for (x in 0..(numX - 1)) {
            for (y in 0..(numY - 1)) {
                val pos = y * numX + x
                if (widgetList[pos] == null) {
                    // TODO
                    return true
                }
            }
        }
        return false
    }

    private fun commitWidgetRearrange() {
        AnimatorSet().apply {
            playTogether(widgetRearrangeAnimators)
            start()
        }
    }

    class WidgetRearrangeTask(val container: WidgetContainerView, val widget: WidgetFrame, val x: Int, val y: Int)
        : AsyncTask<Void, Void, Unit>() {

        override fun doInBackground(vararg params: Void?) {
        }

        override fun onPostExecute(result: Unit?) {
            container.removeView(widget)
            container.addWidget(widget, x, y)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_MOVE -> {
                Log.d("aaaaa2", ev.x.toString() + ev.y.toString())
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