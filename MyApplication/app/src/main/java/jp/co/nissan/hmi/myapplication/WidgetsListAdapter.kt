package jp.co.nissan.hmi.myapplication

import android.util.Log
import android.widget.Adapter
import jp.co.nissan.hmi.myapplication.common.PackageItemInfo
import jp.co.nissan.hmi.myapplication.common.WidgetItem
import jp.co.nissan.hmi.myapplication.util.MultiHashMap

class WidgetsListAdapter: Adapter<WidgetRowViewHolder> {


    fun setWidgets(model: MultiHashMap<PackageItemInfo, WidgetItem>) {
        Log.d("aaaaa", model.toString())
    }
}