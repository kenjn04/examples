package com.example.hmi.myapplication2.temp

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.example.hmi.myapplication2.Launcher
import com.example.hmi.myapplication2.common.LauncherMode
import com.example.hmi.myapplication2.util.DraggingHelper

class WidgetFrame(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle), View.OnLongClickListener {

    private val launcher: Launcher = context as Launcher

    lateinit var widgetContainerView: WidgetContainerView

    var spanX: Int = 1
    var spanY: Int = 1
    var positionX: Int = 1
    var positionY: Int = 1
    var appWidgetId: Int = -1

    private val draggingHelper = DraggingHelper(this)

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, spanX: Int, spanY: Int, id: Int) : this(context, null, 0) {
        this.spanX = spanX
        this.spanY = spanY
        this.appWidgetId = id
    }

    init {
        setOnLongClickListener(this)
    }

    private var dragAllowed = false

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
        return true
    }

    private fun startDragging() {
        widgetContainerView.removeWidget(this)
        launcher.workspace.addView(this)
        translationX += widgetContainerView.relativeTranslationX
        translationY += widgetContainerView.translationY

        launcher.workspace.startWidgetDragging(this)
    }

    fun finishDragging() {
        launcher.workspace.finishWidgetDragging()
        dragAllowed = false
    }

    fun onDragging(x: Float, y: Float) {
        if (dragAllowed and !draggingHelper.dragStarted) {
            draggingHelper.startDragging(x, y)
        } else {
            draggingHelper.movePositionByDrag(x, y, false)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // disable widget touch
        if (launcher.mode == LauncherMode.REARRANGE) {
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is WidgetFrame) {
            if (appWidgetId.equals(other.appWidgetId)) return true
        }
        return false
    }
}