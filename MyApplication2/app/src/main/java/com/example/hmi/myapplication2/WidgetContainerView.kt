package com.example.hmi.myapplication2

import android.content.Context
import android.os.AsyncTask
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.GridLayout
import android.view.Gravity
import android.view.MotionEvent

class WidgetContainerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    private var launcher: Launcher? = null

    private val widgetFrameWidth: Int
    private val widgetFrameHeight: Int

    private val numX: Int = 4
    private val numY: Int = 2

    private var draggingWidget: WidgetFrame? = null

    private val widgetList = mutableListOf<WidgetFrame?>(null, null, null, null, null, null, null, null)

    private val widgetRearrangeTasks = mutableListOf<WidgetRearrangeTask>()

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
        for (i in 0..(widget.spanX - 1)) {
            for (j in 0..(widget.spanY - 1)) {
                val k = (y + j) * numX + x + i
                widgetList[k] = widget
            }
        }
    }

    fun startWidgetDrag(widget: WidgetFrame) {
        widgetRearrangeTasks.clear()
        draggingWidget = widget
    }

    fun endWidgetDrag(): Boolean {
        val layoutParams = draggingWidget!!.layoutParams as FrameLayout.LayoutParams
        val centerWidth = layoutParams.leftMargin + (draggingWidget!!.spanX * widgetFrameWidth) / 2
        val centerHeight = layoutParams.topMargin + (draggingWidget!!.spanY * widgetFrameHeight) / 2

        if (moveWidget(draggingWidget!!, centerWidth / widgetFrameWidth, centerHeight / widgetFrameHeight)) {
            commitWidgetRearrange()
        }
        draggingWidget = null

        // TODO: Is there any case to return false?
        return true
    }

    // TODO: Confirm how to move the widget
    private fun moveWidget(widget: WidgetFrame, x: Int, y: Int): Boolean {
        widgetRearrangeTasks.add(WidgetRearrangeTask(this, widget, x, y))
        for (i in 0..(widget.spanX - 1)) {
            for (j in 0..(widget.spanY - 1)) {
                val k = (y + j) * numX + x + i
                Log.d("aaaaabbbbb", k.toString() + " " + x.toString())
                widgetRearrangeTasks.add(WidgetRearrangeTask(this, widgetList[k]!!, x + i, 1))
            }
        }
        return true
    }

    private fun commitWidgetRearrange() {
        for (task in widgetRearrangeTasks) {
            task.execute()
        }
        widgetRearrangeTasks.clear()
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