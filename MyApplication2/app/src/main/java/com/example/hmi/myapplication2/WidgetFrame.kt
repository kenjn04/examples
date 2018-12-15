package com.example.hmi.myapplication2

import android.content.ClipData
import android.content.Context
import android.text.method.Touch
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout

class WidgetFrame(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    var spanX: Int = 2
    var spanY: Int = 1

    private var launcher: Launcher = context as Launcher

    private var widgetContainer: WidgetContainerView

    private var layoutParams: FrameLayout.LayoutParams? = null

    private var originalLayoutParams: FrameLayout.LayoutParams? = null

    private var originalTouchPosition: TouchPosition? = null

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, spanX: Int, spanY: Int) : this(context, null, 0) {
        this.spanX = spanX
        this.spanY = spanY
    }

    init {
        widgetContainer = launcher.widgetContainer
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        layoutParams = params as LayoutParams
        super.setLayoutParams(params)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_MOVE -> {
                movePositionByDrag(TouchPosition(event.x, event.y))
            }
            MotionEvent.ACTION_DOWN -> {
                startDrag(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                endDrag()
            }
        }
        return true
    }

    private fun movePositionByDrag(currentTouchPosition: TouchPosition) {
        val touchPositionDiff = currentTouchPosition - originalTouchPosition!!
        val newLayoutParams = FrameLayout.LayoutParams(originalLayoutParams).apply {
            leftMargin += touchPositionDiff.left.toInt()
            topMargin += touchPositionDiff.top.toInt()
        }
        super.setLayoutParams(newLayoutParams)
    }

    private fun startDrag(x: Float, y: Float) {
        originalTouchPosition = TouchPosition(x, y)
        originalLayoutParams = layoutParams

        /** To move the widget most front */
        widgetContainer.removeView(this)
        widgetContainer.addView(this)

        /** notify WidgetContainer to start drag with which widget */
        widgetContainer.startWidgetDrag(this)
    }

    private fun endDrag() {
        if (!widgetContainer.endWidgetDrag()) {
            layoutParams = originalLayoutParams
            super.setLayoutParams(layoutParams)
        }
        originalTouchPosition = null
        originalLayoutParams = null
    }

    class TouchPosition(val left: Float, val top: Float) {
        operator fun minus(position: TouchPosition): TouchPosition {
            return TouchPosition(left - position.left, top - position.top)
        }
    }
}