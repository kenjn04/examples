package com.example.hmi.myapplication2

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.view.MotionEvent
import com.example.hmi.myapplication2.util.SwipeDetector

class Workspace(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    private val launcher: Launcher = context as Launcher

    lateinit var containerConnector: WidgetContainerConnector

    private val swipeDetector = SwipeDetector()

    var shiftX: Float = 0F

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onFinishInflate() {
        super.onFinishInflate()
        containerConnector = findViewById(R.id.widget_container_connector)

        shiftX = 0.5F * launcher.params.displaySize.x
        translationX = - shiftX
    }

    fun setWidgetContainers(containers: MutableList<WidgetContainerView>) {
        containerConnector.widgetContainers = containers
    }

    fun shrink() {
        val shrinkAnimator = createShrinkAnimator()
        shrinkAnimator.start()
    }

    private fun createShrinkAnimator(): ObjectAnimator {

        var holderX = PropertyValuesHolder.ofFloat("scaleX", 0.8F)
        var holderY = PropertyValuesHolder.ofFloat("scaleY", 0.8F)
        var objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, holderX, holderY)
        objectAnimator.duration = 100

        return objectAnimator
    }

    private fun onSwipe(right: Boolean, velocity: Float) {
        containerConnector.transitContainerHolder(right, velocity)
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