package com.example.hmi.myapplication2

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import android.view.View
import com.example.hmi.myapplication2.common.LauncherMode
import com.example.hmi.myapplication2.common.LauncherParams
import com.example.hmi.myapplication2.preview.*
import com.example.hmi.myapplication2.preview.common.LauncherAppWidgetHost
import com.example.hmi.myapplication2.preview.common.LauncherAppWidgetProviderInfo
import com.example.hmi.myapplication2.preview.common.PackageItemInfo
import com.example.hmi.myapplication2.preview.view.WidgetItem
import com.example.hmi.myapplication2.temp.WidgetContainerConnector
import com.example.hmi.myapplication2.temp.WidgetFrame
import com.example.hmi.myapplication2.temp.Workspace
import com.example.hmi.myapplication2.util.MultiHashMap

class Launcher : AppCompatActivity() {

    private val APP_WIDGET_HOST_ID = 12345

    /** Managers */
    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var userManager: UserManager

    /** Views */
    lateinit var workspace: Workspace
    private lateinit var widgetPreview: WidgetPreviewView

    /** Request Code */
    private val REQUEST_BIND_APPWIDGET = 1

    /** Others */
    lateinit var params: LauncherParams
    private lateinit var containerConnector: WidgetContainerConnector
    lateinit var appWidgetHost: AppWidgetHost
    lateinit var installedWidgets: MultiHashMap<PackageItemInfo, WidgetItem>

    var mode = LauncherMode.DISPLAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        params = LauncherParams(this)
        setContentView(R.layout.launcher)

        setManagers()
        appWidgetHost.startListening()

        setViews()

        installedWidgets = getWidgetsList()

        setCurrentWidgets()

        bindAllWidgets(installedWidgets)

        transitMode(mode)
    }

    private fun setManagers() {
        appWidgetManager = AppWidgetManager.getInstance(this)
        userManager = getSystemService(Context.USER_SERVICE) as UserManager
        appWidgetHost = LauncherAppWidgetHost(this, APP_WIDGET_HOST_ID)
    }

    private fun setViews() {
        workspace = findViewById(R.id.workspace)
        containerConnector = findViewById(R.id.widget_container_connector)
        widgetPreview = findViewById(R.id.widget_preview)

        workspace.visibility = View.GONE
    }

    private fun setCurrentWidgets() {
        val pName = "com.android.chrome"
        val cName = "org.chromium.chrome.browser.searchwidget.SearchWidgetProvider"
        val id = 0
        val x = 1
        val y = 0

        val packageItemInfo = PackageItemInfo(pName)
        val componentName = ComponentName(pName, cName)

        val itemList = installedWidgets[packageItemInfo]!!
        var loader: WidgetHostViewLoader? = null
        for (item in itemList) {
            if (item.componentName == componentName) {
                loader = WidgetHostViewLoader(this, item.widgetInfo, id, x, y)
                break
            }
        }
        loader!!.loadWidget()
    }

    private val colors = listOf(
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW,
        Color.BLACK
    )
    private fun setDataForTest() {

        var j: Int = 0
        var k: Int = 1
        for (i in 1..(params.widgetContainerNum - 1)) {
            val frame1 = WidgetFrame(this, 1, 1, k++)
            val frame2 = WidgetFrame(this, 1, 1, k++)
//            val frame3 = WidgetFrame(this, 1, 1, k++)
            val frame4 = WidgetFrame(this, 2, 2, k++)

            frame1.setBackgroundColor(colors[j++ % colors.size])
            frame2.setBackgroundColor(colors[j++ % colors.size])
//            frame3.setBackgroundColor(colors[j++ % colors.size])
            frame4.setBackgroundColor(colors[j++ % colors.size])

            containerConnector.addWidget(frame1, i - 1, 0, 0)
            containerConnector.addWidget(frame2, i - 1, 1, 0)
//            containerConnector.addWidget(frame3, i - 1, 0, 1)
            containerConnector.addWidget(frame4, i - 1, 2, 0)
        }
//        transitMode(LauncherMode.REARRANGE)
    }

    fun transitMode(nextMode: LauncherMode) {
        when (nextMode) {
            LauncherMode.DISPLAY -> {
                workspace.visibility = View.VISIBLE
                widgetPreview.visibility = View.GONE

                workspace.unShrink()
            }
            LauncherMode.REARRANGE -> {
                workspace.visibility = View.VISIBLE
                widgetPreview.visibility = View.GONE

                workspace.shrink()
            }
            LauncherMode.SELECT -> {
                workspace.visibility = View.GONE
                widgetPreview.visibility = View.VISIBLE
            }
        }
        mode = nextMode
    }

    private fun bindAllWidgets(widgets: MultiHashMap<PackageItemInfo, WidgetItem>) {
        widgetPreview.setWidgets(widgets)
    }

    fun onAppWidgetInflated(hostView: AppWidgetHostView, id: Int, x: Int, y: Int) {
        var j = 1
        var k = 1
        val frame = WidgetFrame(this, 1, 1, k++)
        frame.setBackgroundColor(colors[j++ % colors.size])
        containerConnector.addWidget(frame, id, x, y)
        frame.addView(hostView)

        transitMode(LauncherMode.DISPLAY)
        Log.d("aaabbbccc", hostView.toString())
    }

    /** Maybe this should be done in model layer */
    private fun getWidgetsList(): MultiHashMap<PackageItemInfo, WidgetItem> {
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

        return widgetsList
    }

    private fun getWidgetInfoFromAllProviders(): MutableList<AppWidgetProviderInfo> {
        val providers = mutableListOf<AppWidgetProviderInfo>()
        for (user in userManager.userProfiles) {
            providers.addAll(appWidgetManager.getInstalledProvidersForProfile(user))
        }
        return providers
    }

    val pendingWidgets
            = mutableMapOf<Int, Pair<LauncherAppWidgetProviderInfo, WidgetHostViewLoader>>()
    fun requestAppWidgetBind(appWidgetId: Int, pInfo: LauncherAppWidgetProviderInfo, loader: WidgetHostViewLoader) {

        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, pInfo.provider)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER_PROFILE, pInfo.profile)

        pendingWidgets[appWidgetId] = Pair(pInfo, loader)
        startActivityForResult(intent, REQUEST_BIND_APPWIDGET)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_BIND_APPWIDGET -> {
                    val appWidgetId = data!!.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID
                    )
                    val info = pendingWidgets[appWidgetId]
                    pendingWidgets.remove(appWidgetId)
                    info!!.second.onRequestCompleted(info.first)
                }
            }
        }
    }
}
