package jp.co.sample.hmi.home.view.preview

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import jp.co.sample.hmi.home.R
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.view.HomeActivity

class WidgetSelectionView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
) : FrameLayout(context, attrs, defStyle), View.OnClickListener, View.OnLongClickListener
{
    private val home = context as HomeActivity

    private val adapter: WidgetsListAdapter =
        WidgetsListAdapter(context,this, this)

    private lateinit var recyclerView: RecyclerView

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onFinishInflate() {
        super.onFinishInflate()
        recyclerView = findViewById(R.id.widgets_list_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    fun setWidgets(widgets: List<HomeAppWidgetProviderInfo>) {
        adapter.setWidgets(widgets)
        adapter.notifyDataSetChanged();
    }

    override fun onClick(view: View?) {
        if (view is WidgetPreviewCell) {
            val previewCell = view as WidgetPreviewCell
            home.addWidget(previewCell.pInfo.provider)
        }
    }

    override fun onLongClick(view: View?): Boolean {
        // Nothing to do
        return false
    }
}

