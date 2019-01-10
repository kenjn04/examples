package com.example.hmi.myapplication2.preview

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.example.hmi.myapplication2.Launcher
import com.example.hmi.myapplication2.R
import com.example.hmi.myapplication2.preview.common.PackageItemInfo
import com.example.hmi.myapplication2.preview.view.WidgetItem
import com.example.hmi.myapplication2.preview.view.WidgetPreviewCell
import com.example.hmi.myapplication2.preview.view.WidgetsListAdapter
import com.example.hmi.myapplication2.util.MultiHashMap

class WidgetPreviewView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
) : FrameLayout(context, attrs, defStyle), View.OnClickListener, View.OnLongClickListener
{
    private val launcher = context as Launcher

    private val adapter: WidgetsListAdapter =
        WidgetsListAdapter(this, this, context)

    private lateinit var recyclerView: RecyclerView

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

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
        val info: PendingAppWidgetInfo = view!!.tag as PendingAppWidgetInfo
        val loader = WidgetHostViewLoader(launcher, info.info)
        loader.loadWidget()
        return true
    }
}

