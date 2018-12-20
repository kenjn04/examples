package com.example.hmi.myapplication2.util

import android.view.View

class DraggingHelper(val view: View, val dragY: Boolean = true) {

    private var originalTouchPosition: TouchPosition? = null

    private var originalTranslationX: Float? = null
    private var originalTranslationY: Float? = null

    var dragStarted = false

    fun startDragging(x: Float, y: Float): Boolean {
        originalTouchPosition = TouchPosition(x, y)
        originalTranslationX = view.translationX
        originalTranslationY = view.translationY

        dragStarted = true

        return true
    }

    fun movePositionByDrag(x: Float, y: Float) {

        if (!dragStarted) {
            // nothing to do
            return
        }

        val currentTouchPosition = TouchPosition(x, y)
        val touchPositionDiff = currentTouchPosition - originalTouchPosition!!
        view.translationX += touchPositionDiff.x
        if (dragY) {
            view.translationY += touchPositionDiff.y
        }
    }

    fun endDragging(revert: Boolean) {
        if (revert) {
            view.translationX = originalTranslationX!!
            view.translationY = originalTranslationY!!
        }
        originalTouchPosition = null
        originalTranslationX = null
        originalTranslationY = null

        dragStarted = false
    }

    class TouchPosition(val x: Float, val y: Float) {
        operator fun minus(position: TouchPosition): TouchPosition {
            return TouchPosition(x - position.x, y - position.y)
        }
    }
}