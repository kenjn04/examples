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
    private lateinit var containerConnector: WidgetContainerConnector

    private var shiftX: Float = 0F
    private var shiftY: Float = 0F
    private val scale = 0.8F
    private val animationDurationMS = 100L

    private var draggingWidget: WidgetFrame? = null

    private val swipeDetector = SwipeDetector()

    private var connectorTransitAlready = false

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

        var holderX = PropertyValuesHolder.ofFloat("scaleX", scale)
        var holderY = PropertyValuesHolder.ofFloat("scaleY", scale)
        var objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, holderX, holderY)
        objectAnimator.duration = animationDurationMS

        return objectAnimator
    }

    fun startWidgetDragging(widget: WidgetFrame) {
        draggingWidget = widget
        containerConnector.startWidgetDragging(widget)
    }

    fun finishWidgetDragging() {
        draggingWidget = null
        containerConnector.finishWidgetDragging()
    }

    private fun onSwipe(right: Boolean, velocity: Float) {
        containerConnector.transitContainer(right, velocity)
    }

    private fun onWidgetDragging(x: Float, y: Float) {
        draggingWidget?.onDragging(x, y)
        if ((x - shiftX) < 0) {
            if (!connectorTransitAlready) {
                containerConnector.transitContainer(true, null)
                connectorTransitAlready = true
            }
        } else if ((x - shiftX) > launcher.params.displaySize.x) {
            if (!connectorTransitAlready) {
                containerConnector.transitContainer(false, null)
                connectorTransitAlready = true
            }
        } else {
            connectorTransitAlready = false
        }
        containerConnector.moveShadowFrame()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_MOVE -> {
                if (draggingWidget != null) return true
            }
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
            MotionEvent.ACTION_MOVE -> {
                onWidgetDragging(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_UP -> {
                draggingWidget?.finishDragging()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}