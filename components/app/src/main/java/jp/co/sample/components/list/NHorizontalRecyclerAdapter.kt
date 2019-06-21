package jp.co.sample.components.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.co.sample.components.R
import jp.co.sample.componentslibrary.list.NRecyclerAdapter

class NHorizontalRecyclerAdapter(
        context: Context,
        list: MutableList<Int>
): NRecyclerAdapter<NHorizontalRecyclerAdapter.ViewHolder>(list) {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val type = ViewType.fromInt(viewType)
        return when (ViewType.fromInt(viewType)) {
            ViewType.BODY -> {
                BodyViewHolder(
                    inflater.inflate(
                        R.layout.fruit_view,
                        parent,
                        false
                    ), type
                )
            }
            ViewType.HEADER -> {
                HeaderViewHolder(
                    inflater.inflate(
                        R.layout.horizontal_header_view,
                        parent,
                        false
                    ), type
                )
            }
            ViewType.FOOTER -> {
                FooterViewHolder(
                    inflater.inflate(
                        R.layout.horizontal_footer_view,
                        parent,
                        false
                    ), type
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.type) {
            ViewType.BODY -> {
                val holder = holder as? BodyViewHolder
                        ?: throw AssertionError("Invalid class: ${holder}")
                holder.apply {
                    image.setImageResource(list[position - 1])
                    text.text = position.toString()
                }
            }
            // Nothing to do
            else -> {}
        }
    }

    abstract class ViewHolder(view: View, val type: ViewType): RecyclerView.ViewHolder(view)

    class BodyViewHolder(view: View, type: ViewType): ViewHolder(view, type) {
        val image: ImageView by lazy { view.findViewById<ImageView>(R.id.imageView) }
        val text: TextView by lazy { view.findViewById<TextView>(R.id.textView) }
    }

    class HeaderViewHolder(view: View, type: ViewType): ViewHolder(view, type)
    class FooterViewHolder(view: View, type: ViewType): ViewHolder(view, type)

}