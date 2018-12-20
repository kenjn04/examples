package com.example.hmi.myapplication2

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.view.MotionEvent
import android.view.View
import com.example.hmi.myapplication2.util.DraggingHelper
import java.lang.Math.abs

class Workspace(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    private val launcher = context as Launcher

    private val displaySize: Point

    private var currentMainContainer = 0

    private val widgetContainerHolders = mutableListOf<FrameLayout>()

    var widgetDragging = false

    private var duringTransition = false

    private val draggingHelper = DraggingHelper(this, false)

    var scale = 1F

    var widgetContainerViews: MutableList<WidgetContainerView> = mutableListOf()
        set(containers) {
            field = containers
            initializeWidgetContainers()
        }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        // set parameters
        displaySize = launcher.params.displaySize

        // set layout
        layoutParams = FrameLayout.LayoutParams(displaySize.x * 3, displaySize.y)
    }

    private fun initializeWidgetContainers() {
        for (container in widgetContainerViews) {
            val widgetContainerHolder = FrameLayout(context).apply {
                layoutParams = FrameLayout.LayoutParams(displaySize.x, displaySize.y)
                setBackgroundColor(Color.GRAY)
            }
            container.apply {
                translationX = (displaySize.x - container.layoutParams.width).toFloat() / 2
                translationY = (displaySize.y - container.layoutParams.height).toFloat() / 2
                setBackgroundColor(Color.WHITE)
            }
            widgetContainerHolder.addView(container)
            addView(widgetContainerHolder)
            widgetContainerHolders.add(widgetContainerHolder)
        }
        reLayoutWidgetContainer()
    }

    private fun reLayoutWidgetContainer() {
        translationX = - displaySize.x.toFloat()
        for (widgetContainerHolder in widgetContainerHolders) {
            widgetContainerHolder.visibility = View.GONE
            widgetContainerHolder.translationX = 0F
        }
        for (i in -1..1) {
            val j = (currentMainContainer + i + widgetContainerHolders.size) % widgetContainerHolders.size
            val k = i + 1
            val widgetContainerHolder = widgetContainerHolders[j]
            widgetContainerHolder.apply {
                translationX = (k * displaySize.x).toFloat()
                visibility = View.VISIBLE
            }
        }
    }

    private fun transitContainerHolderIfRequired() {
        val translationDiffX = translationX + displaySize.x
        if (abs(translationDiffX) < (displaySize.x.toFloat() * 0.5 * scale)) {
            return
        }
        if (translationDiffX < 0) {
            transitContainerHolder(false, null)
        } else {
            transitContainerHolder(true, null)
        }
    }

    fun transitContainerHolder(right: Boolean, velocity: Float?) {
        if (duringTransition) return
        if (right) {
            duringTransition = true
            createTransitAnimator(displaySize.x * scale - displaySize.x, velocity).start()
            currentMainContainer = (currentMainContainer + widgetContainerHolders.size - 1) % widgetContainerHolders.size
        } else {
            duringTransition = true
            createTransitAnimator(-(displaySize.x + displaySize.x * scale), velocity).start()
            currentMainContainer = (currentMainContainer + 1) % widgetContainerHolders.size
        }
    }

    var aaa: Float? = null
    private fun createTransitAnimator(toX: Float, velocity: Float?): ObjectAnimator {

        var holderX = PropertyValuesHolder.ofFloat("translationX", translationX, toX)
        var objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, holderX)
        if (velocity != null) {
            objectAnimator.duration = (abs(translationX - toX) / velocity).toLong()
        } else {
            objectAnimator.duration = 1000
        }

        objectAnimator.addListener(
            object: Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }
                override fun onAnimationEnd(animation: Animator?) {
                    reLayoutWidgetContainer()
                    duringTransition = false
                }
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationRepeat(animation: Animator?) {}

            }
        )
        return objectAnimator
    }

    private var draggingWidget: WidgetFrame? = null

    fun startWidgetDrag(widget: WidgetFrame) {
        draggingWidget = widget
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!widgetDragging) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!duringTransition) transitContainerHolderIfRequired()
                if (!draggingHelper.dragStarted) {
                    return draggingHelper.startDragging(event.x, event.y)
                } else {
                    draggingHelper.movePositionByDrag(event.x, event.y)
                }
            }
            MotionEvent.ACTION_DOWN -> {
                if (!widgetDragging) {
                    return draggingHelper.startDragging(event.x, event.y)
                }
            }
            MotionEvent.ACTION_UP -> {
                draggingHelper.endDragging(true)
            }
            MotionEvent.ACTION_CANCEL -> {
                draggingHelper.endDragging(false)
            }
        }
        return super.onTouchEvent(event)
    }
}