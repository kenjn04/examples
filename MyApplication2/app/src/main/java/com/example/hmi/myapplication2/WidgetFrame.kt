package com.example.hmi.myapplication2

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.example.hmi.myapplication2.common.LauncherMode
import com.example.hmi.myapplication2.util.DraggingHelper

class WidgetFrame(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle), View.OnLongClickListener, View.OnTouchListener {

    var spanX: Int = 1
    var spanY: Int = 1

    private val launcher: Launcher = context as Launcher

    lateinit var widgetContainerView: WidgetContainerView

    private val draggingHelper = DraggingHelper(this)

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, spanX: Int, spanY: Int) : this(context, null, 0) {
        this.spanX = spanX
        this.spanY = spanY
    }

    init {
        setOnLongClickListener(this)
        setOnTouchListener(this)
    }

    private var dragEnabled = false

    private val TAG = "WidgetFrame"
    override fun onLongClick(v: View?): Boolean {
        when (launcher.mode) {
            LauncherMode.DISPLAY -> {
                launcher.transitMode(LauncherMode.REARRANGE)
            }
            LauncherMode.REARRANGE -> {
                launcher.workspace.widgetDragging = true
                dragEnabled = true
            }
        }
        Log.d("aaabba " + TAG, "OnLongClick")
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_MOVE -> {
                Log.d("aaabbb " + TAG, "ACTION_MOVE")
            }
            MotionEvent.ACTION_DOWN -> {
                Log.d("aaabbb " + TAG, "ACTION_DOWN")
            }
            MotionEvent.ACTION_UP -> {
                Log.d("aaabbb " + TAG, "ACTION_UP")
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d("aaabbb " + TAG, "ACTION_CANCEL")
            }
        }
        return false
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if (view !is WidgetFrame) return false
        if (launcher.mode != LauncherMode.REARRANGE) return false

        when (event!!.action) {
            MotionEvent.ACTION_MOVE -> {
                Log.d("aaabbd " + TAG, "ACTION_MOVE")
                if (dragEnabled and !draggingHelper.dragStarted) {
                    startDragging()
                    draggingHelper.startDragging(event.x, event.y)
                    return true
                } else {
                    draggingHelper.movePositionByDrag(event.x, event.y)
                }
                Log.d("AAAAAAAAA", translationX.toString())
            }
            MotionEvent.ACTION_DOWN -> {
                Log.d("aaabbdddd " + TAG, "ACTION_DOWN " + translationX + " " + translationY)
                widgetContainerView.removeView(this)
                translationX += 60
                translationY += 0
                launcher.launcherFrame.addView(this)
                Log.d("aaabbdddd " + TAG, "ACTION_DOWN " + translationX + " " + translationY)
                if (dragEnabled) {
                    startDragging()
                    return draggingHelper.startDragging(event.x, event.y)
               }
            }
            MotionEvent.ACTION_UP -> {
                Log.d("aaabbd " + TAG, "ACTION_UP")
                draggingHelper.endDragging(false)
                dragEnabled = false
                launcher.workspace.widgetDragging = false
                if (draggingHelper.dragStarted) {
                    widgetContainerView.endWidgetDrag()
                }
            }
        }
        return false
    }

    private fun startDragging() {

        /** To move the widget most front */
//        widgetContainerView.removeView(this)
//        widgetContainerView.addView(this)

        /** notify WidgetContainerView to start drag with this view */
        launcher.workspace.startWidgetDrag(this)
    }

    private val SWIPE_MIN_DISTANCE = 120
    private val SWIPE_MAX_OFF_PATH = 250
    private val SWIPE_THRESHOLD_VELOCITY = 200
}