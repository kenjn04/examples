package com.sample.myapplication

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.view.MotionEvent

class PanelView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle),
ContainerView.OnTransChangeListener {

    private val TAG = "PanelView"

    /** For Animation */
    private val MAXIMUM_VELOSITY = 15 // (=pixel(?)/msec)
    private val MAXIMUM_ANDLE = 90
    private val monitoredIntervalMs = ContainerView.MONITORING_INTERVAL_MS

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun OnTransChange(xDiff: Float) {
        val velocity = 0F // xDiff / monitoredIntervalMs
        Log.d(TAG, "${translationX} ${xDiff}  ${velocity}")

        // TODO: Why there will be difference in translationX?
        val currentTranslationX = translationX
        val nextTranslationX = currentTranslationX + xDiff

        val rotationAngle = velocity / MAXIMUM_VELOSITY * MAXIMUM_ANDLE
        val transX = PropertyValuesHolder.ofFloat("translationX", currentTranslationX, nextTranslationX)
        val rotateY = PropertyValuesHolder.ofFloat("rotationY", rotationAngle)
        ObjectAnimator.ofPropertyValuesHolder(this, transX, rotateY).apply {
            duration = (monitoredIntervalMs * 0.95).toLong() // 0.95 is to avoid next notify
            start()
        }
    }
}