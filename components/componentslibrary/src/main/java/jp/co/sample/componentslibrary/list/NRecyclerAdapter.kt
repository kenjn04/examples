package jp.co.sample.componentslibrary.list

import android.util.Log
import androidx.recyclerview.widget.RecyclerView

abstract class NRecyclerAdapter<T: RecyclerView.ViewHolder>(
        protected val list: MutableList<Int>
): RecyclerView.Adapter<T>() {

    override fun getItemCount() = list.size + 2

    override fun getItemViewType(position: Int): Int {
        if ((position > 0) and (position < 16)) Log.d("aaabbbcccddd2", position.toString() + " " +  list[position - 1].toString())
        return when (position) {
            0 -> ViewType.HEADER
            (itemCount - 1) -> ViewType.FOOTER
            else -> ViewType.BODY
        }.int
    }

    fun moveItem(from: Int, to: Int) {
        val from = from - 1
        val to = to - 1
        val fromItem = list[from]
        list.removeAt(from)
        list.add(to, fromItem)
    }

    enum class ViewType(val int: Int) {
        BODY(0),
        HEADER(1),
        FOOTER(2);

        companion object {
            fun fromInt(index: Int): ViewType {
                return values().firstOrNull { it.int == index } ?: BODY
            }
        }
    }
}