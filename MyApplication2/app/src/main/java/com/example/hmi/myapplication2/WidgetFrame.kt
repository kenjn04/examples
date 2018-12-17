package com.example.hmi.myapplication2

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class WidgetFrame(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    var spanX: Int = 1
    var spanY: Int = 1

    private var launcher: Launcher = context as Launcher

    private lateinit var widgetContainer: WidgetContainer

    private var originalTouchPosition: TouchPosition? = null

    private var originalTranslationX: Float? = null
    private var originalTranslationY: Float? = null

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, spanX: Int, spanY: Int) : this(context, null, 0) {
        this.spanX = spanX
        this.spanY = spanY
    }

    init {
//        widgetContainer = launcher.widgetContainer
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
        translationX += touchPositionDiff.x
        translationY += touchPositionDiff.y
    }

    private fun startDrag(x: Float, y: Float) {
        originalTouchPosition = TouchPosition(x, y)
        originalTranslationX = translationX
        originalTranslationY = translationY

        /** To move the widget most front */
        widgetContainer.removeView(this)
        widgetContainer.addView(this)

        /** notify WidgetContainer to start drag with this view */
        widgetContainer.startWidgetDrag(this)
    }

    private fun endDrag() {
        if (!widgetContainer.endWidgetDrag()) {
            translationX = originalTranslationX!!
            translationY = originalTranslationY!!
        }
        originalTouchPosition = null
        originalTranslationX = null
        originalTranslationY = null
    }

    class TouchPosition(val x: Float, val y: Float) {
        operator fun minus(position: TouchPosition): TouchPosition {
            return TouchPosition(x - position.x, y - position.y)
        }
    }
}