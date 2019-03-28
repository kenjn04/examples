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
                fixEdgePosition(recyclerView)
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun fixEdgePosition(recyclerView: RecyclerView) {
        val layoutManager = getLayoutManager(recyclerView)
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()

        // Nothing to do if there is no item
        if (getItemCount(recyclerView) == 0) return

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
        if (dx != 0) {
            recyclerView.smoothScrollBy(dx, 0)
        }
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
        return getAdapter(recyclerView).itemCount
    }

    private fun getLayoutManager(recyclerView: RecyclerView): LinearLayoutManager {
        return recyclerView.layoutManager!! as? LinearLayoutManager
                ?: throw AssertionError("Only AnimateLinearLayoutManager is supported.")
    }

    private fun getAdapter(recyclerView: RecyclerView): AnimateRecyclerAdapter {
        return recyclerView.adapter!! as? AnimateRecyclerAdapter
                ?: throw AssertionError("Only AnimateRecyclerAdapter is supported.")
    }

}
