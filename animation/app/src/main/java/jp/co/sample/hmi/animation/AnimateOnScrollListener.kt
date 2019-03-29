package jp.co.sample.hmi.animation

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Math.abs

class AnimateOnScrollListener: RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        addAnimation(recyclerView)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                alignEdgePosition(recyclerView)
            }
            // Nothing to do
            else -> {}
        }
    }

    private fun alignEdgePosition(recyclerView: RecyclerView) {
        // Nothing to do if there is no item
        if (getItemCount(recyclerView) == 0) return

        when (getScrollType(recyclerView)) {
            AnimateRecyclerView.ScrollMode.DOT -> {
                alignEdgePositionDot(recyclerView)
            }
            AnimateRecyclerView.ScrollMode.PAGE -> {
                alignEdgePositionPage(recyclerView)
            }
        }
    }

    private fun alignEdgePositionPage(recyclerView: RecyclerView) {
        val layoutManager = getLayoutManager(recyclerView)
        val first = layoutManager.findFirstVisibleItemPosition()

        val targetPosition = getCurrentPosition(recyclerView)
        val width = recyclerView.getChildAt(1).width
        val offset = -recyclerView.getChildAt(0).left
        /**
         * Suppose header and footer width is same sa body width
         */
        val dx: Int = if (first < targetPosition) {
            (targetPosition - first) * width - offset
        } else {
            -((first - targetPosition) * width + offset)
        }
        smoothScrollBy(recyclerView, dx, 0)
    }

    private fun alignEdgePositionDot(recyclerView: RecyclerView) {
        val layoutManager = getLayoutManager(recyclerView)
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()

        val dx: Int = if (isHeader(first)) {
            val secondView = recyclerView.getChildAt(1)
            secondView.left
        } else if (isFooter(recyclerView, last)) {
            val lastView = recyclerView.getChildAt(last - first)
            lastView.left - recyclerView.width
        } else {
            val firstView = recyclerView.getChildAt(0)
            if (abs(firstView.left) > (firstView.width / 2)) {
                firstView.left + firstView.width
            } else {
                firstView.left
            }
        }
        smoothScrollBy(recyclerView, dx, 0)
        updateCurrentPosition(recyclerView, layoutManager.findFirstVisibleItemPosition())
    }

    private fun smoothScrollBy(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if ((dx == 0) and (dy == 0)) return
        recyclerView.smoothScrollBy(dx, dy)
    }

    private fun updateCurrentPosition(recyclerView: RecyclerView, position: Int) {
        val recyclerView = recyclerView as? AnimateRecyclerView
                ?: throw AssertionError("Invalid class: ${recyclerView}")
        recyclerView.currentPosition = position
    }

    private fun addAnimation(recyclerView: RecyclerView) {
        val layoutManager = getLayoutManager(recyclerView)
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()
        setAlphaToEachItem(recyclerView, first, last)
    }

    private fun setAlphaToEachItem(recyclerView: RecyclerView, first: Int, last: Int) {
        val firstView = recyclerView.getChildAt(0)
        val lastView = recyclerView.getChildAt(last - first)

        // Set alpha other than header
        if (!isHeader(first)) {
            firstView.alpha = 1 - abs(firstView.left.toFloat() / firstView.width)
        }
        var offset = firstView.width +firstView.left
        ((first + 1)..(last - 1)).forEach {
            recyclerView.getChildAt(it - first).apply {
                alpha = 1.0F
                offset += this.width
            }
        }
        // Set alpha other than hooter
        if (!isFooter(recyclerView, last)) {
            lastView.alpha = abs((recyclerView.width - offset).toFloat() / lastView.width)
        }
    }

    private fun isHeader(position: Int) = position == 0

    private fun isFooter(recyclerView: RecyclerView, position: Int): Boolean {
        return position == (getItemCount(recyclerView) - 1)
    }

    private fun getItemCount(recyclerView: RecyclerView): Int {
        return recyclerView.adapter!!.itemCount
    }

    private fun getScrollType(recyclerView: RecyclerView): AnimateRecyclerView.ScrollMode {
        val recyclerView = recyclerView as? AnimateRecyclerView
                ?: throw AssertionError("Invalid class: ${recyclerView}")
        return recyclerView.scrollMode
    }

    private fun getCurrentPosition(recyclerView: RecyclerView): Int {
        val recyclerView = recyclerView as? AnimateRecyclerView
                ?: throw AssertionError("Invalid class: ${recyclerView}")
        return recyclerView.currentPosition
    }

    private fun getLayoutManager(recyclerView: RecyclerView): LinearLayoutManager {
        return recyclerView.layoutManager!! as? LinearLayoutManager
                ?: throw AssertionError("Only LinearLayoutManager is supported as of now.")
    }
}
