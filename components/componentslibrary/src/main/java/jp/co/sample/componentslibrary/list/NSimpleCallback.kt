package jp.co.sample.componentslibrary.list

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class NSimpleCallback(
    dragDirs: Int, swipeDirs: Int
) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val from = viewHolder.adapterPosition ?: 0
        val to = target.adapterPosition ?: 0

        val adapter = recyclerView.adapter as? NRecyclerAdapter
            ?: throw AssertionError("Only NRecyclerView is supported.")
        adapter.moveItem(from, to)
        adapter.notifyItemMoved(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }
}
