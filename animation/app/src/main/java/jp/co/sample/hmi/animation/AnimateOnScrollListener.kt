package jp.co.sample.hmi.animation

import android.util.Log
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
                Log.d("aaabbbccc2", "SCROLL_STATE_IDLE")
            }
            RecyclerView.SCROLL_STATE_DRAGGING -> {
                Log.d("aaabbbccc2", "SCROLL_STATE_DRAGING")
            }
            RecyclerView.SCROLL_STATE_SETTLING -> {
                Log.d("aaabbbccc2", "SCROLL_STATE_SETTLING")
            }
        }
    }

    private fun addAnimation(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager!! as? AnimateLinearLayoutManager
                ?: throw AssertionError("Only AnimateLinearLayoutManager is supported as of now.")
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()

        Log.d("aaabbbccc1", "${first} ${last}")
        setAlphaToEachItem(recyclerView, first, last)
    }

    private fun setAlphaToEachItem(recyclerView: RecyclerView, first: Int, last: Int) {

        val firstView = recyclerView.getChildAt(0)
        val lastView = recyclerView.getChildAt(last - first)

        firstView.alpha = 1 - abs(firstView.left.toFloat() / firstView.width)
        var offset = firstView.width +firstView.left
        ((first + 1)..(last - 1)).forEach {
            recyclerView.getChildAt(it - first).apply {
                alpha = 1.0F
                offset += this.width
            }
        }
        lastView.alpha = abs((recyclerView.width - offset).toFloat() / lastView.width)
    }
}
