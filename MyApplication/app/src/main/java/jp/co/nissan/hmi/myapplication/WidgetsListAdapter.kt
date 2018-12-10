package jp.co.nissan.hmi.myapplication

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import jp.co.nissan.hmi.myapplication.common.PackageItemInfo
import jp.co.nissan.hmi.myapplication.common.WidgetItem
import jp.co.nissan.hmi.myapplication.util.MultiHashMap

class WidgetsListAdapter: RecyclerView.Adapter<WidgetRowViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WidgetRowViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(p0: WidgetRowViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun setWidgets(model: MultiHashMap<PackageItemInfo, WidgetItem>) {
        Log.d("aaaaa", model.toString())
    }
}