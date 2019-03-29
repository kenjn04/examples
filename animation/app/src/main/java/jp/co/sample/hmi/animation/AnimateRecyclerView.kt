package jp.co.sample.hmi.animation

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

// TODO: androidx.recyclerview.widget.OrientationHelper may be helpful. Need to check
class AnimateRecyclerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): RecyclerView(context, attrs, defStyle) {

    enum class ScrollMode {
        PAGE, DOT
    }

    var scrollMode = ScrollMode.PAGE
    var currentPosition = 1

    private val VELOCITY_THRESHOLD = 500

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        scrollToPosition(1)
    }

    fun switchScrollMode() {
        scrollMode = when (scrollMode) {
            ScrollMode.DOT -> {
                ScrollMode.PAGE
            }
            ScrollMode.PAGE -> {
                ScrollMode.DOT
            }
        }
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        if (abs(velocityX) < VELOCITY_THRESHOLD) return false

        val layoutManager = layoutManager as? LinearLayoutManager
                ?: throw AssertionError("")
        val first = layoutManager.findFirstVisibleItemPosition()

        val bodyWidth = getChildAt(1).width
        val bodyCount = width / bodyWidth
        val offset = getChildAt(0).left
        val itemCount = adapter!!.itemCount
        when (scrollMode) {
            ScrollMode.PAGE -> { pageFling(velocityX, first, bodyWidth, bodyCount, offset, itemCount) }
            ScrollMode.DOT  -> { dotFling(velocityX, first, bodyWidth, bodyCount, offset, itemCount) }
        }
        return true
    }

    private fun pageFling(velocityX: Int, first: Int, bodyWidth: Int, bodyCount: Int, offset: Int, itemCount: Int) {
        var nextPosition = currentPosition
        var dx = 0
        if (velocityX > 0) {
            nextPosition += bodyCount
            dx = bodyWidth * (nextPosition - first) + offset
            nextPosition = min(nextPosition, itemCount - bodyCount - 1)
        } else {
            nextPosition -= bodyCount
            dx = bodyWidth * (first - nextPosition) - offset
            dx *= -1
            nextPosition = max(nextPosition, 1)
        }
        smoothScrollBy(dx, 0)
        currentPosition = nextPosition
    }

    private fun dotFling(velocityX: Int, first: Int, bodyWidth: Int, bodyCount: Int, offset: Int, itemCount: Int) {
        var nextPosition = 0
        var dx = (getDistance(velocityX) / width) * width
        if (velocityX > 0) {
            nextPosition = first + dx / bodyWidth
            dx += offset
            nextPosition = min(nextPosition, itemCount - bodyCount - 1)
        } else {
            nextPosition = first - dx / bodyWidth
            dx -= (bodyWidth + offset)
            dx *= -1
            nextPosition = max(nextPosition, 1)
        }
        smoothScrollBy(dx, 0)
        currentPosition = nextPosition
    }

    private fun getDistance(velocity: Int): Int {
        return abs(velocity / 2)
    }
}