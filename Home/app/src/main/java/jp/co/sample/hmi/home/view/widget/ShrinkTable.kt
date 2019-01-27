package jp.co.sample.hmi.home.view.widget

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.view.MotionEvent
import android.widget.Button
import jp.co.sample.hmi.home.R
import jp.co.sample.hmi.home.util.SwipeDetector
import jp.co.sample.hmi.home.view.HomeActivity

class ShrinkTable(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    /** Parameters */
    private var shiftX: Float = 0F
    private val scale = 0.8F
    private val animationDurationMS = 100L

    private val home: HomeActivity = context as HomeActivity
    private lateinit var containerConnector: WidgetContainerConnector

    private var draggingWidget: WidgetViewCell? = null

    private val swipeDetector = SwipeDetector()

    private var connectorTransitAlready = false

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onFinishInflate() {
        super.onFinishInflate()

        Log.d("aaabbbccc", width.toString() + " " + height.toString())
        containerConnector = findViewById(R.id.widget_container_connector)

        shiftX = 0.5F * home.params.displaySize.x
        translationX = - shiftX
    }

    fun shrink() {
        val shrinkAnimator = createShrinkAnimator()
        shrinkAnimator.start()
    }

    fun unShrink() {
        scaleX = 1.0F
        scaleY = 1.0F
    }

    private fun createShrinkAnimator(): ObjectAnimator {

        var holderX = PropertyValuesHolder.ofFloat("scaleX", scale)
        var holderY = PropertyValuesHolder.ofFloat("scaleY", scale)
        var objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, holderX, holderY)
        objectAnimator.duration = animationDurationMS

        return objectAnimator
    }

    fun startWidgetDragging(widget: WidgetViewCell) {
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
        } else if ((x - shiftX) > home.params.displaySize.x) {
            if (!connectorTransitAlready) {
                containerConnector.transitContainer(false, null)
                connectorTransitAlready = true
            }
        } else {
            connectorTransitAlready = false
        }
        containerConnector.onWidgetDragging()
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