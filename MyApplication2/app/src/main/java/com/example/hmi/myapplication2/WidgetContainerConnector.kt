package com.example.hmi.myapplication2

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.view.MotionEvent
import android.view.View
import com.example.hmi.myapplication2.util.DraggingHelper
import java.lang.Math.abs

class WidgetContainerConnector(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle), Animator.AnimatorListener {

    private val launcher = context as Launcher

    private val displaySize: Point = launcher.params.displaySize
    private val shiftX: Float = 1.5F * displaySize.x
    val relativeTranslationX: Float

    private var currentMainContainer = 0

    private var duringTransition = false

    private var draggingWidget: WidgetFrame? = null

    private val draggingHelper = DraggingHelper(this, false)

    private val scale: Float
        get() = scaleX

    var widgetContainers: MutableList<WidgetContainerView> = mutableListOf()
        set(containers) {
            field = containers
            initializeWidgetContainers()
        }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        // TODO:
        relativeTranslationX = - 1.0F * displaySize.x // workspace.shiftX - shiftX
    }

    private fun initializeWidgetContainers() {
        for (container in widgetContainers) {
            addView(container)
        }
        reLayoutWidgetContainer()
    }

    private fun reLayoutWidgetContainer() {
        translationX = relativeTranslationX
        for (container in widgetContainers) {
            container.visibility = View.GONE
            container.translationX = 0F
            container.relativeTranslationX = 0F
        }
        for (i in -2..2) {
            val j = (currentMainContainer + i + widgetContainers.size) % widgetContainers.size
            val k = i + 1
            val container = widgetContainers[j]
            container.apply {
                translationX = shiftX + i * displaySize.x
                visibility = View.VISIBLE
            }
            container.initialize(this)
        }
    }

    private fun transitContainerIfRequired() {
        val translationDiffX = translationX - relativeTranslationX
        if (abs(translationDiffX) < (displaySize.x.toFloat() * 0.5 * scale)) {
            return
        }
        if (translationDiffX < 0) {
            transitContainer(false, null)
        } else {
            transitContainer(true, null)
        }
    }

    fun transitContainer(right: Boolean, velocity: Float?) {
        if (duringTransition) return
        if (draggingWidget != null) {
            widgetContainers[currentMainContainer].finishWidgetDrag(false)
        }

        if (right) {
            duringTransition = true
            createTransitContainerAnimator(displaySize.x * scale + relativeTranslationX, velocity).start()
            currentMainContainer = (currentMainContainer + widgetContainers.size - 1) % widgetContainers.size
        } else {
            duringTransition = true
            createTransitContainerAnimator(-(displaySize.x * scale - relativeTranslationX), velocity).start()
            currentMainContainer = (currentMainContainer + 1) % widgetContainers.size
        }

        if (draggingWidget != null) {
            widgetContainers[currentMainContainer].startWidgetDrag(draggingWidget!!)
        }
    }

    private fun createTransitContainerAnimator(toX: Float, velocity: Float?): ObjectAnimator {

        var holderX = PropertyValuesHolder.ofFloat("translationX", translationX, toX)
        var objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, holderX)
        if (velocity != null) {
            objectAnimator.duration = (abs(translationX - toX) / velocity).toLong()
        } else {
            objectAnimator.duration = 1000
        }
        objectAnimator.addListener(this)

        return objectAnimator
    }

    override fun onAnimationStart(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
        reLayoutWidgetContainer()
        duringTransition = false
    }

    override fun onAnimationCancel(animation: Animator?) {}
    override fun onAnimationRepeat(animation: Animator?) {}

    fun startWidgetDragging(widget: WidgetFrame) {
        draggingWidget = widget
        widgetContainers[currentMainContainer].startWidgetDrag(widget)
    }

    fun finishWidgetDragging() {
        draggingWidget = null
        widgetContainers[currentMainContainer].finishWidgetDrag(true)
    }

    fun onWidgetDragging() {
        val container = widgetContainers[currentMainContainer]
        container.onWidgetDragging()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_MOVE -> {
                if (draggingWidget == null) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!duringTransition) transitContainerIfRequired()
                if (!draggingHelper.dragStarted) {
                    return draggingHelper.startDragging(event.x, event.y)
                } else {
                    draggingHelper.movePositionByDrag(event.x, event.y)
                }
            }
            MotionEvent.ACTION_DOWN -> {
                return draggingHelper.startDragging(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                draggingHelper.finishDragging(true)
            }
            MotionEvent.ACTION_CANCEL -> {
                draggingHelper.finishDragging(false)
            }
        }
        return super.onTouchEvent(event)
    }
}