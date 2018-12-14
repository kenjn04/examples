package jp.co.nissan.hmi.myapplication.widgetselection

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import jp.co.nissan.hmi.myapplication.R
import jp.co.nissan.hmi.myapplication.common.PackageItemInfo
import jp.co.nissan.hmi.myapplication.common.WidgetItem
import jp.co.nissan.hmi.myapplication.drag.PendingItemDragHelper
import jp.co.nissan.hmi.myapplication.util.MultiHashMap

class WidgetContainerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
) : FrameLayout(context, attrs, defStyle), View.OnClickListener, View.OnLongClickListener
{
    private val adapter: WidgetsListAdapter = WidgetsListAdapter(this, this, context)

    private lateinit var recyclerView: RecyclerView

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onClick(view: View?) {
        // TODO: Status check here
        handleClick(view)
    }

    private fun handleClick(view: View?) {
        // nothing to do now
    }

    override fun onLongClick(view: View?): Boolean {
        // TODO: Status check here
        return handleLongClick(view)
    }

    private fun handleLongClick(view: View?): Boolean {
        return beginDragging(view)
    }

    private fun beginDragging(view: View?): Boolean {
        if (view is WidgetCell) {
            if (!beginDraggingWidget(view as WidgetCell)) {
                return false
            }
        }
        return true
    }

    private fun beginDraggingWidget(view: WidgetCell): Boolean {
        val image = view.findViewById<ImageView>(R.id.widget_preview)

        PendingItemDragHelper(view).startDrag()
        return true
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        recyclerView = findViewById(R.id.widgets_list_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    fun setWidgets(widgets: MultiHashMap<PackageItemInfo, WidgetItem>) {
        adapter.setWidgets(widgets)
        adapter.notifyDataSetChanged();
    }
}

