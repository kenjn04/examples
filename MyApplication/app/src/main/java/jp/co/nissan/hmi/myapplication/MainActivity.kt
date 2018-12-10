package jp.co.nissan.hmi.myapplication

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import jp.co.nissan.hmi.myapplication.common.LauncherAppWidgetProviderInfo
import jp.co.nissan.hmi.myapplication.common.PackageItemInfo
import jp.co.nissan.hmi.myapplication.common.WidgetItem
import jp.co.nissan.hmi.myapplication.util.MultiHashMap

class MainActivity : AppCompatActivity() {

    private val APP_WIDGET_HOST_ID = 12345

    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var userManager: UserManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appWidgetManager = AppWidgetManager.getInstance(this)
        userManager = getSystemService(Context.USER_SERVICE) as UserManager

        getWidgetsList {widgets ->
            bindAllWidgets(widgets)
        }
    }

    private fun bindAllWidgets(widgets: MultiHashMap<PackageItemInfo, WidgetItem>) {
        for ((k, v) in widgets) {
            Log.d("aaaaabbbbb5", k.packageName)
            for (v1 in v) {
                Log.d("aaaaabbbbb6", v1.componentName.toString())
            }
        }
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
        Log.d("aaaaa", providers.toString())
        return providers
    }

}
