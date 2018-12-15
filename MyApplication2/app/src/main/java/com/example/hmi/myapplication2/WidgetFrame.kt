package com.example.hmi.myapplication2

import android.content.ClipData
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout

class WidgetFrame(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    private var launcher: Launcher? = null

    var spanX: Int = 2
    var spanY: Int = 1

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, spanX: Int, spanY: Int) : this(context, null, 0) {
        this.spanX = spanX
        this.spanY = spanY
    }

    init {
        if (context is Launcher) {
            launcher = context as Launcher
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }
}