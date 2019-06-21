package jp.co.sample.componentslibrary.list

import androidx.recyclerview.widget.RecyclerView
import java.lang.Math.abs
import java.lang.Math.max

/**
 * Note: Current implementation is done with assumption that the width of header
 *       and footer are same as that of body.
 */
class NOnScrollListener: RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val recyclerView = recyclerView as? NRecyclerView
                ?: throw AssertionError("Only AnimateRecyclerView is supported.")
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                alignEdgePosition(recyclerView)
            }
            // Nothing to do
            else -> {}
        }
    }

    private fun alignEdgePosition(recyclerView: NRecyclerView) {
        // Nothing to do if there is no item
        if (recyclerView.getTotalItemCount() == 0) return

        when (recyclerView.scrollMode) {
            NRecyclerView.ScrollMode.DOT -> {
                alignEdgePositionDot(recyclerView)
            }
            NRecyclerView.ScrollMode.PAGE -> {
                alignEdgePositionPage(recyclerView)
            }
        }
    }

    private fun alignEdgePositionPage(recyclerView: NRecyclerView) {
        recyclerView.smoothScrollToDestinationItem(recyclerView.currentPosition)
    }

    private fun alignEdgePositionDot(recyclerView: NRecyclerView) {
        val helper = recyclerView.helper
        val layoutManager = recyclerView.layoutManager!!

        val firstItem = layoutManager.findFirstVisibleItemPosition()
        val lastItem = layoutManager.findLastVisibleItemPosition()

        val destItem: Int = if (isHeader(firstItem)) {
            1
        } else if (isFooter(recyclerView, lastItem)) {
            max(0, recyclerView.getLastLeftEdgeItem())
        } else {
            val firstView = recyclerView.getChildAt(0)
            if (abs(helper.getDecoratedStart(firstView)) < abs(helper.getDecoratedEnd(firstView))) {
                firstItem
            } else {
                firstItem + 1
            }
        }
        recyclerView.smoothScrollToDestinationItem(destItem)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val recyclerView = recyclerView as? NRecyclerView
                ?: throw AssertionError("Only AnimateRecyclerView is supported.")
        super.onScrolled(recyclerView, dx, dy)
        addAnimation(recyclerView)
    }

    private fun addAnimation(recyclerView: NRecyclerView) {
        if (recyclerView.isHorizontalScroll()) {
            addHorizontalAnimation(recyclerView)
        }
    }

    private fun addHorizontalAnimation(recyclerView: NRecyclerView) {
        val layoutManager = recyclerView.layoutManager!!
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()

        val firstView = recyclerView.getChildAt(0)
        val lastView = recyclerView.getChildAt(last - first)

        val helper = recyclerView.helper

        // Set alpha other than header
        if (!isHeader(first)) {
            firstView.alpha = helper.getDecoratedEnd(firstView).toFloat() / helper.getDecoratedMeasurement(firstView)
        }
        // Set alpha to each item
        ((first + 1)..(last - 1)).forEach {
            recyclerView.getChildAt(it - first).apply {
                alpha = 1.0F
            }
        }
        // Set alpha other than hooter
        if (!isFooter(recyclerView, last)) {
            lastView.alpha = (helper.end - helper.getDecoratedStart(lastView)).toFloat() / helper.getDecoratedMeasurement(lastView)
        }
    }

    private fun isHeader(position: Int) = position == 0

    private fun isFooter(recyclerView: NRecyclerView, position: Int): Boolean {
        return position == (recyclerView.getTotalItemCount() - 1)
    }
}
