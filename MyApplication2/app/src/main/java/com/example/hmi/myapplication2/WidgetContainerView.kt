package com.example.hmi.myapplication2

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.GridLayout
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout



class WidgetContainerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    private var launcher: Launcher? = null

    private val widgetFrameWidth: Int
    private val widgetFrameHeight: Int

    val params: GridLayout.LayoutParams
        get() = this.layoutParams as GridLayout.LayoutParams

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        if (context is Launcher) {
            launcher = context as Launcher
        }
        widgetFrameWidth = resources.getDimensionPixelSize(R.dimen.widget_frame_width)
        widgetFrameHeight = resources.getDimensionPixelSize(R.dimen.widget_frame_height)
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
        layoutParams.leftMargin = x * widgetFrameWidth
        layoutParams.topMargin = y * widgetFrameHeight
        widget.layoutParams = layoutParams
        addView(widget)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_MOVE -> {
                Log.d("aaaaa2_i", ev.x.toString() + ev.y.toString())
            }
            MotionEvent.ACTION_DOWN -> {
                Log.d("aaaaa2_i", "ACTION_DOWN")
                performClick()
            }
            MotionEvent.ACTION_UP -> {
                Log.d("aaaaa2_i", "ACTION_UP")
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d("aaaaa2_i", "ACTION_CANCEL")
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}