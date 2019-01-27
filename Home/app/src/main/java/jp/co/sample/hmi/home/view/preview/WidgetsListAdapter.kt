package jp.co.sample.hmi.home.view.preview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.co.sample.hmi.home.R
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo

class WidgetsListAdapter(context: Context): RecyclerView.Adapter<WidgetPreviewHolder>() {

    private val entries = mutableListOf<HomeAppWidgetProviderInfo>()

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    // set layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetPreviewHolder {
        val previewCell = layoutInflater.inflate(R.layout.widget_preview_cell, parent, false) as WidgetPreviewCell

        return WidgetPreviewHolder(previewCell)
    }

    // set text, drawable etc.
    override fun onBindViewHolder(holder: WidgetPreviewHolder, pos: Int) {
        val pInfo = entries[pos]

        val widget = holder.previewCell
        widget.applyFromCellItem(pInfo)
        widget.ensurePreview()
        widget.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int = entries.size

    fun setWidgets(widgets: List<HomeAppWidgetProviderInfo>) {
        entries.clear()
        entries.addAll(widgets.sortedBy { it.widgetLabel })
    }
}