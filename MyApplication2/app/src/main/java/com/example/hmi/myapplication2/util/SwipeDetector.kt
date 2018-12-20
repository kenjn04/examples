package com.example.hmi.myapplication2.util

import java.lang.Math.abs

class SwipeDetector {

    private var startTouchMS: Long = -1L
    private var startTouchX: Float = -1F
    private var startTouchY: Float = -1F

    private val thresholdX: Float = 500F
    private val thresholdY: Float = 150F
    private val thresholdMS: Long = 200

    var velocity: Float? = null

    fun onTouch(x: Float, y: Float) {
        velocity = null
        startTouchX = x
        startTouchY = y
        startTouchMS = System.currentTimeMillis()
    }

    fun getSwipeDirectionIfSwiped(x: Float, y: Float): SwipeDirection {
        val diffX = startTouchX - x
        val diffY = startTouchY - y
        val diffMS = System.currentTimeMillis() - startTouchMS
        if ((abs(diffX) < thresholdX) or (abs(diffY) > thresholdY) or (diffMS > thresholdMS)) {
            return SwipeDirection.None
        }

        velocity = abs(diffX / diffMS)
        return if (diffX > 0) {
            SwipeDirection.Left
        } else {
            SwipeDirection.Right
        }
    }

    enum class SwipeDirection {
        Right,
        Left,
        None
    }
}