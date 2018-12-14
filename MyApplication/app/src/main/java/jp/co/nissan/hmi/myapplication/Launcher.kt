package jp.co.nissan.hmi.myapplication

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.UserManager
import android.widget.LinearLayout
import jp.co.nissan.hmi.myapplication.common.LauncherAppWidgetProviderInfo
import jp.co.nissan.hmi.myapplication.common.PackageItemInfo
import jp.co.nissan.hmi.myapplication.common.WidgetItem
import jp.co.nissan.hmi.myapplication.drag.DragController
import jp.co.nissan.hmi.myapplication.util.MultiHashMap
import jp.co.nissan.hmi.myapplication.widgethost.LauncherAppWidgetHost
import jp.co.nissan.hmi.myapplication.widgetselection.WidgetContainerView

class Launcher : AppCompatActivity() {

    private val APP_WIDGET_HOST_ID = 12345

    /**
     * Managers
     */
    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var userManager: UserManager


    /**
     * Views
     */
    lateinit var dragLayer: LinearLayout
    private lateinit var widgetsView: WidgetContainerView

    /**
     * Others
     */
    lateinit var appWidgetHost: AppWidgetHost
    val dragController = DragController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher)

        setManagers()
        setViews()

        getWidgetsList {widgets ->
            bindAllWidgets(widgets)
        }
    }

    private fun setManagers() {
        appWidgetManager = AppWidgetManager.getInstance(this)
        userManager = getSystemService(Context.USER_SERVICE) as UserManager
        appWidgetHost = LauncherAppWidgetHost(this, APP_WIDGET_HOST_ID)
    }

    private fun setViews() {
        dragLayer = findViewById(R.id.drag_layer)
        widgetsView = findViewById(R.id.widget_view)
    }

    private fun bindAllWidgets(widgets: MultiHashMap<PackageItemInfo, WidgetItem>) {
        widgetsView.setWidgets(widgets)
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
                            LauncherAppWidgetProviderInfo.fromProviderInfo(this, widgetInfo),
                            packageManager
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

    companion object {
        // TODO: need to revisit
        fun getLauncher(context: Context): Launcher? {
            if (context is Launcher) {
                return context as Launcher
            }
            return null
        }
    }
}
