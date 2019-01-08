package com.example.hmi.myapplication2.preview.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hmi.myapplication2.R
import com.example.hmi.myapplication2.preview.common.PackageItemInfo
import com.example.hmi.myapplication2.util.MultiHashMap

class WidgetsListAdapter(
    val onClickListener: View.OnClickListener,
    val onLongClickListener: View.OnLongClickListener,
    context: Context
): RecyclerView.Adapter<WidgetRowViewHolder>() {

    private val entries = mutableListOf<WidgetListPackageEntry>()

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    // set layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetRowViewHolder {
        val container = layoutInflater.inflate(R.layout.widget_preview_container, parent, false) as ViewGroup

        return WidgetRowViewHolder(container)
    }

    // set text, drawable etc.
    override fun onBindViewHolder(holder: WidgetRowViewHolder, pos: Int) {
        val entry = entries[pos]
        val infoList = entry.widgets

        val container = holder.cellContainer
        for (info in infoList) {
            val widget = layoutInflater.inflate(R.layout.widget_preview_cell, container, false) as WidgetPreviewCell

            widget.setOnClickListener(onClickListener)
            widget.setOnLongClickListener(onLongClickListener)
            container.addView(widget)

            widget.applyFromCellItem(info)
            widget.ensurePreview()
            widget.visibility = View.VISIBLE

            widget.gravity = Gravity.CENTER
        }
    }

    override fun getItemCount(): Int = entries.size

    fun setWidgets(widgets: MultiHashMap<PackageItemInfo, WidgetItem>) {
        entries.clear()
        // handling widget by grouping with package. (This may not be required.)
        for ((key, value) in widgets) {
            // TODO: maybe some sorting is required for row?
            val entry = WidgetListPackageEntry(key, value)
            entries.add(entry)
        }
        // TODO: maybe some sorting is required for entries?
    }
}