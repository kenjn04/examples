package jp.co.sample.hmi.home.util

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

    fun movePositionByDrag(x: Float, y: Float, relative: Boolean = true) {

        if (!dragStarted) {
            // nothing to do
            return
        }

        val currentTouchPosition = TouchPosition(x, y)
        val touchPositionDiff = currentTouchPosition - originalTouchPosition!!

        if (relative) {
            view.translationX += touchPositionDiff.x
        } else {
            view.translationX = originalTranslationX!! + touchPositionDiff.x
        }
        if (dragY) {
            if (relative) {
                view.translationY += touchPositionDiff.y
            } else {
                view.translationY = originalTranslationY!! + touchPositionDiff.y
            }
        }
    }

    fun finishDragging(revert: Boolean) {
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