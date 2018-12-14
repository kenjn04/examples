package jp.co.nissan.hmi.myapplication

import android.content.Context
import android.support.v7.widget.RecyclerView.Adapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.co.nissan.hmi.myapplication.common.PackageItemInfo
import jp.co.nissan.hmi.myapplication.common.WidgetItem
import jp.co.nissan.hmi.myapplication.util.MultiHashMap

class WidgetsListAdapter(
    context: Context
): Adapter<WidgetRowViewHolder>() {

    private val entries = mutableListOf<WidgetListRowEntry>()

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    // set layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetRowViewHolder {
        val container = layoutInflater.inflate(R.layout.widgets_list_row_view, parent, false) as ViewGroup

        Log.d("aaabbbccc3", "afafdafda")
        return WidgetRowViewHolder(container)
    }

    // set text, drawable etc.
    override fun onBindViewHolder(holder: WidgetRowViewHolder, pos: Int) {
        val entry = entries[pos]
        val infoList = entry.widgets

        Log.d("aaabbbccc3", "afafdafda")

        val row = holder.cellContainer
        for (info in infoList) {
            val widget = layoutInflater.inflate(R.layout.widget_cell, row, false) as WidgetCell
            row.addView(widget)

            widget.applyFromCellItem(info)
            widget.ensurePreview()
            widget.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = entries.size

    fun setWidgets(widgets: MultiHashMap<PackageItemInfo, WidgetItem>) {
        entries.clear()
        // handling widget by grouping with package. (This may not be required.)
        for ((key, value) in widgets) {
            // TODO: maybe some sorting is required for row?
            val row = WidgetListRowEntry(key, value)
            entries.add(row)
        }
        // TODO: maybe some sorting is required for entries?
        for ((k, v) in widgets) {
            Log.d("aaabbbccc1", k.packageName)
            for (v1 in v) {
                Log.d("aaabbbccc2", v1.componentName.toString() + " " + entries.size)
            }
        }
    }
}