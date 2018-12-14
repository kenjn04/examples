package jp.co.nissan.hmi.myapplication.widgetselection

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import jp.co.nissan.hmi.myapplication.R

class WidgetRowViewHolder(view: ViewGroup): RecyclerView.ViewHolder(view) {

    val cellContainer: ViewGroup
    val title: TextView

    init {
        cellContainer = view.findViewById(R.id.widgets_cell_list)
        title = view.findViewById(R.id.section)
    }

}