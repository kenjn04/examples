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

    private var dragAllowed = false

    private val TAG = "WidgetFrame"
    override fun onLongClick(v: View?): Boolean {
        when (launcher.mode) {
            LauncherMode.DISPLAY -> {
                launcher.transitMode(LauncherMode.REARRANGE)
            }
            LauncherMode.REARRANGE -> {
                startDragging()
                dragAllowed = true
            }
        }
        Log.d("aaabba " + TAG, "OnLongClick")
        return true
    }

    private fun prepareDragging() {
        widgetContainerView.removeView(this)
        launcher.workspace.addView(this)
        translationX += widgetContainerView.relativeTranslationX
        translationY += widgetContainerView.translationY
    }

    private fun startDragging() {
        launcher.workspace.startWidgetDragging(this)
        prepareDragging()
    }

    fun finishDragging() {
        launcher.workspace.finishWidgetDragging()
        dragAllowed = false
    }

    // cancelled before drag starting
    private fun cancelDragging() {
        launcher.workspace.removeView(this)
        widgetContainerView.addView(this)
        translationX -= widgetContainerView.relativeTranslationX
        translationY -= widgetContainerView.translationY
        dragAllowed = false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (launcher.mode == LauncherMode.REARRANGE) {
            return true
        }
        return false
    }

    private var aaa: Float = 0F
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if (view !is WidgetFrame) return false
        if (launcher.mode != LauncherMode.REARRANGE) return false
        when (event!!.action) {
            MotionEvent.ACTION_MOVE -> {
                Log.d("aaabbb " + TAG, "ACTION_MOVE " + event.x + " " + (event.x - aaa) + " " + translationX)
                if (dragAllowed and !draggingHelper.dragStarted) {
                    draggingHelper.startDragging(event.x, event.y)
                    return true
                } else {
                    draggingHelper.movePositionByDrag(event.x, event.y)
                }
            }
            MotionEvent.ACTION_DOWN -> {
                Log.d("aaabbb " + TAG, "ACTION_DOWN " + event.x + " " + translationX)
                aaa = event.x
//                prepareDragging()
            }
            MotionEvent.ACTION_UP -> {
                Log.d("aaabbb " + TAG, "ACTION_UP " + translationX)
                finishDragging()
                draggingHelper.finishDragging(false)
                Log.d("aaabbb " + TAG, "ACTION_UP " + translationX)
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d("aaabbb " + TAG, "ACTION_CANCEL")
//                cancelDragging()
            }
        }
        return false
    }

    fun onDragging(x: Float, y: Float) {
        if (dragAllowed and !draggingHelper.dragStarted) {
            draggingHelper.startDragging(x, y)
        } else {
            draggingHelper.movePositionByDrag(x, y, false)
        }
    }
}