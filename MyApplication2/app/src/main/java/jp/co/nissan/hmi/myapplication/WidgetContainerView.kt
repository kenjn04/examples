package jp.co.nissan.hmi.myapplication

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import jp.co.nissan.hmi.myapplication.common.PackageItemInfo
import jp.co.nissan.hmi.myapplication.common.WidgetItem
import jp.co.nissan.hmi.myapplication.util.MultiHashMap

class WidgetContainerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
) : FrameLayout(context, attrs, defStyle), View.OnClickListener, View.OnLongClickListener
{
//    private val adapter: WidgetsListAdapter = WidgetsListAdapter(this, this, context)
    private val adapter: WidgetsListAdapter = WidgetsListAdapter(context)

    private lateinit var recyclerView: RecyclerView
    private lateinit var loader: ProgressBar

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onClick(view: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLongClick(view: View?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val radapter = RAdapter(context)
    private val appsList: MutableList<AppInfo> = mutableListOf()

    override fun onFinishInflate() {
        super.onFinishInflate()
        /*
        recyclerView = findViewById(R.id.widgets_list_view)



        recyclerView.adapter = radapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        myThread().execute()

        loader = findViewById(R.id.loader)
        */
    }

    fun setWidgets(widgets: MultiHashMap<PackageItemInfo, WidgetItem>) {
        adapter.setWidgets(widgets)
//        adapter.notifyDataSetChanged();
        adapter.notifyItemInserted(adapter.itemCount - 1)
        Log.d("===================", "=====================================================================================" + adapter.itemCount)
    }


    inner class myThread : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg Params: Void): String {

            val pm = context.packageManager

            val i = Intent(Intent.ACTION_MAIN, null)
            i.addCategory(Intent.CATEGORY_LAUNCHER)

            val allApps = pm.queryIntentActivities(i, 0)
            for (ri in allApps) {
                val label = ri.loadLabel(pm)
                val packageName = ri.activityInfo.packageName
                val icon = ri.activityInfo.loadIcon(pm)
                val app = AppInfo(label, packageName, icon)
                appsList.add(app)
            }
            radapter.appsList = appsList
            return "Success"

        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            updateStuff()
        }

        private fun updateStuff() {
            radapter.notifyItemInserted(radapter.itemCount - 1)
            Log.d("aaabbbccc", radapter.itemCount.toString())
        }
    }
}

