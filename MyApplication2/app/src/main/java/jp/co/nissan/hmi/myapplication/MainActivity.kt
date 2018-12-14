package jp.co.nissan.hmi.myapplication

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.UserManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import jp.co.nissan.hmi.myapplication.common.LauncherAppWidgetProviderInfo
import jp.co.nissan.hmi.myapplication.common.PackageItemInfo
import jp.co.nissan.hmi.myapplication.common.WidgetItem
import jp.co.nissan.hmi.myapplication.util.MultiHashMap
import kotlinx.android.synthetic.main.widgets_view.*

class MainActivity : AppCompatActivity() {

    private val APP_WIDGET_HOST_ID = 12345

    /**
     * Managers
     */
    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var userManager: UserManager


    /**
     * Views
     */
    private lateinit var widgetsView: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setManagers()
        setViews()

        getWidgetsList {widgets ->
            bindAllWidgets(widgets)
        }
    }

    private fun setManagers() {
        appWidgetManager = AppWidgetManager.getInstance(this)
        userManager = getSystemService(Context.USER_SERVICE) as UserManager
    }


    private val radapter = RAdapter(this)
    private val appsList: MutableList<AppInfo> = mutableListOf()

    private lateinit var adapter: WidgetsListAdapter

    private fun setViews() {
        adapter = WidgetsListAdapter(this)

        widgetsView = findViewById(R.id.widget_view)


        val recyclerView = widgetsView.findViewById<RecyclerView>(R.id.widgets_list_view)
        recyclerView.adapter = radapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        myThread().execute()
    }

    inner class myThread : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg Params: Void): String {

            val pm = packageManager

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

    private fun bindAllWidgets(widgets: MultiHashMap<PackageItemInfo, WidgetItem>) {
 //       adapter.setWidgets(widgets)
 //       adapter.notifyDataSetChanged()
//        widgetsView.setWidgets(widgets)
    }

    /**
     * Maybe this should be done in model layer
     */
    private fun getWidgetsList(callback: (widgets: MultiHashMap<PackageItemInfo, WidgetItem>)->Unit) {
        // get all widgets as WidgetItem
        val widgetItems = mutableListOf<WidgetItem>()
        for (widgetInfo in getWidgetInfoFromAllProviders()) {
            widgetItems.add(
                    WidgetItem(
                            LauncherAppWidgetProviderInfo.fromProviderInfo(this, widgetInfo)
                    )
            )
        }

        // get together with package name
        val widgetsList = MultiHashMap<PackageItemInfo, WidgetItem>()
        val tmpPackageItemInfos = hashMapOf<String, PackageItemInfo>()
        for (item in widgetItems) {
            val packageName = item.packageName
            var pInfo = tmpPackageItemInfos[packageName]
            if (pInfo == null) {
                pInfo = PackageItemInfo(packageName)
                tmpPackageItemInfos[packageName] = pInfo
            }
            pInfo!!
            widgetsList.addToList(pInfo, item)
        }

        callback(widgetsList)
    }

    private fun getWidgetInfoFromAllProviders(): MutableList<AppWidgetProviderInfo> {
        val providers = mutableListOf<AppWidgetProviderInfo>()
        for (user in userManager.userProfiles) {
            providers.addAll(appWidgetManager.getInstalledProvidersForProfile(user))
        }
        return providers
    }

}
