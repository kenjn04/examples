package jp.co.sample.hmi.animation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnimateRecyclerAdapter(
        context: Context,
        private val list: List<Int>
): RecyclerView.Adapter<AnimateRecyclerAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val type = ViewType.fromInt(viewType)
        return ViewHolder(inflater.inflate(R.layout.fruit_view, parent, false), type)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.type) {
            ViewType.BODY -> {
                holder.apply {
                    if ((position != 0) and (position != 11)) image.setImageResource(list[position - 1])
                    text.text = position.toString()
                }
            }
            else -> {
                // Nothing to do as of now
            }
        }
    }

    override fun getItemCount() = list.size + 2

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ViewType.HEADER
            (itemCount - 1) -> ViewType.FOOTER
            else -> ViewType.BODY
        }.int
    }

    inner class ViewHolder(view: View, val type: ViewType): RecyclerView.ViewHolder(view) {
        val image: ImageView by lazy { view.findViewById<ImageView>(R.id.imageView) }
        val text: TextView by lazy { view.findViewById<TextView>(R.id.textView) }
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