package com.sample.myapplication

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.widget.FrameLayout
import android.view.MotionEvent

class ContainerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    private val TAG = "ContainerView"

    /** To monitor and notify translationX change */
    private var previousTranslationX: Float = 0F
    private var zeroReported = true

    private val monitorThread = HandlerThread("")
    private val monitorHandler: Handler

    private val transChangeListners: MutableList<OnTransChangeListener> = mutableListOf()
    companion object {
        val MONITORING_INTERVAL_MS = 100L
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init{
        monitorThread.start()
        monitorHandler = Handler(monitorThread.looper)
        monitorTransChange()
    }

    private fun monitorTransChange() {
        handleTransChange()
        monitorHandler.postDelayed({
            monitorTransChange()
        }, MONITORING_INTERVAL_MS)
    }

    private var sum = 0F
    private fun handleTransChange() {
        val currentTranslationX = translationX
        val translationDiffX = currentTranslationX - previousTranslationX
        sum += translationDiffX
        if (translationDiffX == 0F) {
            if (zeroReported) return
            notifyTransChange(translationDiffX)
            zeroReported = true
        } else {
            notifyTransChange(translationDiffX)
            zeroReported = false
        }
        previousTranslationX = currentTranslationX
    }

    private fun notifyTransChange(xDiff: Float) {
        for (listener in transChangeListners) {
            listener.OnTransChange(xDiff)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    fun registerListener(listener: OnTransChangeListener) {
        transChangeListners.add(listener)
    }

    private var originalX: Float = 0F
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event!!
        val x = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                originalX = x
            }
            MotionEvent.ACTION_MOVE -> {
                translationX += x - originalX

            }
            MotionEvent.ACTION_UP -> {
                translationX = 0F
            }
            else -> {
                // Nothing to do
            }
        }
        return true
    }

    interface OnTransChangeListener {
        fun OnTransChange(xDiff: Float)
    }
}