package com.example.hmi.myapplication2

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.view.MotionEvent
import com.example.hmi.myapplication2.util.SwipeDetector

class LauncherFrame(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    private val swipeDetector = SwipeDetector()

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private fun onSwipe(right: Boolean, velocity: Float) {
        findViewById<Workspace>(R.id.workspace).transitContainerHolder(right, velocity)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> {
                swipeDetector.onTouch(ev.x, ev.y)
            }
            MotionEvent.ACTION_UP -> {
                when (swipeDetector.getSwipeDirectionIfSwiped(ev.x, ev.y)) {
                    SwipeDetector.SwipeDirection.Right -> {
                        onSwipe(true, swipeDetector.velocity!!)
                        return true
                    }
                    SwipeDetector.SwipeDirection.Left -> {
                        onSwipe(false, swipeDetector.velocity!!)
                        return true
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_UP -> {
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}