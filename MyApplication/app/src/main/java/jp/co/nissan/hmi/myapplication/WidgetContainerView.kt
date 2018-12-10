package jp.co.nissan.hmi.myapplication

import jp.co.nissan.hmi.myapplication.common.PackageItemInfo
import jp.co.nissan.hmi.myapplication.common.WidgetItem
import jp.co.nissan.hmi.myapplication.util.MultiHashMap

class WidgetContainerView {

    private val adapter: WidgetsListAdapter

    init {
        adapter = WidgetsListAdapter()
    }

    fun setWidgets(model: MultiHashMap<PackageItemInfo, WidgetItem>) {
        adapter.setWidgets(model)
        adapter.notifyDataSetChanged();
    }
}