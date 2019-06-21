package jp.co.sample.componentslibrary.list

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.OrientationHelper.createHorizontalHelper
import androidx.recyclerview.widget.OrientationHelper.createVerticalHelper
import androidx.recyclerview.widget.RecyclerView
import jp.co.sample.componentslibrary.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class NRecyclerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): RecyclerView(context, attrs, defStyle) {

    enum class ScrollMode {
        PAGE, DOT
    }

    private val HORIZONTAL_HEADER_WIDTH: Int = resources.getDimensionPixelSize(R.dimen.animate_recycler_view_horizontal_header_width)
    private val HORIZONTAL_FOOTER_WIDTH: Int = resources.getDimensionPixelSize(R.dimen.animate_recycler_view_horizontal_footer_width)
    private val VERTICAL_HEADER_HEIGHT: Int = resources.getDimensionPixelSize(R.dimen.animate_recycler_view_vertical_header_height)
    private val VERTICAL_FOOTER_HEIGHT: Int = resources.getDimensionPixelSize(R.dimen.animate_recycler_view_vertical_footer_height)
    private val VELOCITY_THRESHOLD = 500

    val helper: OrientationHelper by lazy {
        if (isHorizontalScroll()) {
            createHorizontalHelper(layoutManager)
        } else {
            createVerticalHelper(layoutManager)
        }
    }

    var scrollMode = ScrollMode.PAGE
    var currentPosition = 1

    var layoutManager: LinearLayoutManager?
        set(value) {
            super.setLayoutManager(value)
        }
        get() {
            return super.getLayoutManager() as LinearLayoutManager?
        }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        addOnScrollListener(NOnScrollListener())
        overScrollMode = View.OVER_SCROLL_NEVER
    }

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

    override fun computeVerticalScrollOffset() = computeScrollOffset(super.computeVerticalScrollOffset())
    override fun computeVerticalScrollRange()  = computeScrollRange(super.computeVerticalScrollRange())
    override fun computeVerticalScrollExtent() = computeScrollExtent(super.computeVerticalScrollExtent())
    override fun computeHorizontalScrollOffset() = computeScrollOffset(super.computeHorizontalScrollOffset())
    override fun computeHorizontalScrollRange()  = computeScrollRange(super.computeHorizontalScrollRange())
    override fun computeHorizontalScrollExtent() = computeScrollExtent(super.computeHorizontalScrollExtent())

    override fun overScrollBy(deltaX: Int, deltaY: Int, scrollX: Int, scrollY: Int, scrollRangeX: Int, scrollRangeY: Int, maxOverScrollX: Int, maxOverScrollY: Int, isTouchEvent: Boolean): Boolean {
        Log.d("aaabbbcc", "${deltaX} ${deltaY} ${scrollX} ${scrollY}")
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent)
    }

    private fun computeScrollOffset(original: Int): Int {
        Log.d("aaabbbccc", "${overScrollMode} ${View.OVER_SCROLL_NEVER}")
        var offset = original - getHeaderSize()
        offset = max(0, offset)
        return offset
    }

    private fun computeScrollRange(original: Int): Int {
        return original - getHeaderSize() - getFooterSize()
    }

    private fun computeScrollExtent(original: Int): Int {
        return original - abs(computeExcessOffset())
    }

    private fun computeExcessOffset(): Int {
        var offset = if (isHorizontalScroll()) {
            super.computeHorizontalScrollOffset()
        } else {
            super.computeVerticalScrollOffset()
        }
        offset -= getHeaderSize()
        val edgeRange = computeRealScrollRange() - if (isHorizontalScroll()) {
            super.computeHorizontalScrollExtent()
        } else {
            super.computeVerticalScrollExtent()
        }
        return when {
            offset < 0         -> offset
            offset > edgeRange -> offset - edgeRange
            else -> 0
        }
    }

    private fun computeRealScrollRange(): Int {
        var range = if (isHorizontalScroll()) {
            super.computeHorizontalScrollRange()
        } else {
            super.computeVerticalScrollRange()
        }
        range -= getHeaderSize() + getFooterSize()
        return range
    }

    private fun getHeaderSize(): Int {
        return if (isHorizontalScroll()) {
            HORIZONTAL_HEADER_WIDTH
        } else {
            VERTICAL_HEADER_HEIGHT
        }
    }

    private fun getFooterSize(): Int {
        return if (isHorizontalScroll()) {
            HORIZONTAL_FOOTER_WIDTH
        } else {
            VERTICAL_FOOTER_HEIGHT
        }
    }

    /**
     * Note: Current implementation is done with assumption that the width of header
     *       and footer are same as that of body.
     */
    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        var velocity = if (isHorizontalScroll()) {
            velocityX
        } else {
            velocityY
        }
        if (abs(velocity) < VELOCITY_THRESHOLD) return false

        when (scrollMode) {
            ScrollMode.PAGE -> {
                pageFling(velocity)
            }
            ScrollMode.DOT -> {
                dotFling(velocity)
            }
        }
        return true
    }

    private fun pageFling(velocity: Int) {
        val moveItemCount = getVisibleItemCount()
        val destItem = if (velocity > 0) {
            min(currentPosition + moveItemCount, getLastLeftEdgeItem())
        } else {
            max(1, currentPosition - moveItemCount)
        }
        smoothScrollToDestinationItem(destItem)
    }

    private fun dotFling(velocity: Int) {
        val first = layoutManager!!.findFirstVisibleItemPosition()
        var moveItemCount = calculateDistanceFromVelocity(velocity) / getItemSize()
        val destItem = if (velocity > 0) {
            first + moveItemCount
        } else {
            first - moveItemCount
        }
        smoothScrollToDestinationItem(destItem)
    }

    private fun getItemSize(): Int {
        return helper.getDecoratedMeasurement(getChildAt(1))
    }

    private fun getWholeSize(): Int {
        return helper.end
    }

    private fun getVisibleItemCount(): Int {
        return getWholeSize() / getItemSize()
    }

    fun getTotalItemCount(): Int {
        return  adapter!!.itemCount
    }

    fun getLastLeftEdgeItem(): Int {
        return getTotalItemCount() - getVisibleItemCount() - 1
    }

    fun isHorizontalScroll(): Boolean {
        return layoutManager!!.canScrollHorizontally()
    }

    private fun calculateDistanceFromVelocity(velocity: Int): Int {
        return abs(velocity / 2)
    }

    fun smoothScrollToDestinationItem(destItem: Int) {
        val distance = calculateDistance(destItem)
        if (distance == 0) return
        if (isHorizontalScroll()) {
            smoothScrollBy(distance, 0)
        } else {
            smoothScrollBy(0, distance)
        }
        currentPosition = destItem
    }

    /**
     *  Distance is the below:
     *    |<- offset ->|<--       distance       -->|
     *    |            |   firstItem  |              |   destItem   |
     *    |            |<-   size   ->|<-   size   ->|<-   size   ->|
     */
    private fun calculateDistance(destItem: Int): Int {
        val layoutManager = layoutManager!!

        var firstItem = layoutManager.findFirstVisibleItemPosition()
        val firstView = if (firstItem == 0) {
            firstItem++
            getChildAt(1)
        } else {
            getChildAt(0)
        }

        val size = helper.getDecoratedMeasurement(firstView)
        val offset = helper.getDecoratedStart(firstView)
        return (destItem - firstItem) * size + offset
    }
}