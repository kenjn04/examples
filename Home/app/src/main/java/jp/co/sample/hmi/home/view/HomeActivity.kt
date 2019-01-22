package jp.co.sample.hmi.home.view

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.R
import jp.co.sample.hmi.home.common.WidgetIdProvider
import jp.co.sample.hmi.home.repository.db.WidgetItemInfo
import jp.co.sample.hmi.home.util.WidgetHostViewLoader
import jp.co.sample.hmi.home.view.preview.WidgetSelectionView
import jp.co.sample.hmi.home.view.widget.WidgetContainerConnector
import jp.co.sample.hmi.home.view.widget.WidgetViewCell
import jp.co.sample.hmi.home.view.widget.ShrinkTable
import jp.co.sample.hmi.home.viewmodel.HomeViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {

    private val TAG = "HomeActivity"

    private val APP_WIDGET_HOST_ID = 12345

    private val homeViewModel: HomeViewModel by viewModel()

    lateinit var appWidgetHost: AppWidgetHost

    private var installedWidgetList: List<HomeAppWidgetProviderInfo> = listOf()
    private var currentWidgetList: List<WidgetItemInfo> = listOf()
    private val homeModeChangeListeners = hashMapOf<Int, HomeModeChangeListener>()
    private val pendingWidgets
            = mutableMapOf<Int, Pair<HomeAppWidgetProviderInfo, WidgetHostViewLoader>>()

    /** Views */
    lateinit var shrinkTable: ShrinkTable
    private lateinit var widgetSelectionView: WidgetSelectionView
    private lateinit var containerConnector: WidgetContainerConnector
    private lateinit var backButton: Button
    private lateinit var customBottons: LinearLayout
    private lateinit var addButton: Button
    private lateinit var rearrangeButton: Button


    /** Request Code */
    private val REQUEST_BIND_APPWIDGET = 1

    lateinit var params: HomeParams

    var mode = HomeMode.DISPLAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        params = HomeParams(this)
        setContentView(R.layout.activity_home)

        appWidgetHost = AppWidgetHost(this, APP_WIDGET_HOST_ID)
        // TODO: need to stop somewhere (need to check Activity lifecycle)
        appWidgetHost.startListening()

        setViews()

        updateInstalledWidgetList()
        setCurrentWidgets()
    }

    private fun setViews() {
        shrinkTable = findViewById(R.id.workspace)
        widgetSelectionView = findViewById(R.id.widget_preview)
        containerConnector = findViewById(R.id.widget_container_connector)
        backButton = findViewById(R.id.back_button)
        customBottons = findViewById(R.id.custom_buttons)
        addButton = customBottons.findViewById(R.id.add_button)
        rearrangeButton = customBottons.findViewById(R.id.rearrange_button)

        backButton.setOnClickListener {
            transitMode(HomeMode.DISPLAY)
        }

        addButton.setOnClickListener {
            transitMode(HomeMode.SELECTION)
        }

        rearrangeButton.setOnClickListener {
            containerConnector.rearrangeWidgets()
        }

        homeModeChangeListeners[0] = containerConnector
        transitMode(mode)
    }

    // TODO: Maybe need to be called when application is added and removed to get latest lists
    private fun updateInstalledWidgetList() {
        installedWidgetList = homeViewModel.installedWidgetList
        widgetSelectionView.setWidgets(installedWidgetList)
    }

    fun addWidget(componentName: ComponentName) {
        val addItem = containerConnector.widgetAddCell.item
        val item = WidgetItemInfo(addItem).apply {
            id = WidgetIdProvider.getInstance().getId()
            packageName = componentName.packageName
            className = componentName.className
        }
        /** After adding from db, the updateCurrentWidgets will be called by LiveData */
        homeViewModel.addWidget(item)
        transitMode(HomeMode.DISPLAY)
    }

    fun deleteWidget(item: WidgetItemInfo) {
        val appWidgetId = item.appWidgetId!!
        appWidgetHost.deleteAppWidgetId(appWidgetId)
        homeModeChangeListeners.remove(appWidgetId)
        /** After deleting from db, the updateCurrentWidgets will be called by LiveData */
        homeViewModel.deleteWidget(item)
    }

    fun updateWidget(items: List<WidgetItemInfo>) {
        homeViewModel.updateWidget(items)
    }

    private fun setCurrentWidgets() {
        val currentWidgetsLiveData = homeViewModel.currentWidgets
        updateCurrentWidgets(currentWidgetList)
        currentWidgetsLiveData.observeForever {
            updateCurrentWidgets(it)
        }
    }

    private fun updateCurrentWidgets(currentWidgets: List<WidgetItemInfo>?) {
        val previousWidgetList = currentWidgetList.toList()
        val latestWidgetList = currentWidgets?.toList() ?: listOf<WidgetItemInfo>()

        val addedWidgetList = mutableListOf<WidgetItemInfo>()
        val deletedWidgetList = currentWidgetList.toMutableList()
        val updatedWidgetList = mutableListOf<Pair<WidgetItemInfo, WidgetItemInfo>>()

        for (lWidget in latestWidgetList) {
            var found: Boolean = false
            for (pWidget in previousWidgetList) {
                if (lWidget == pWidget) {
                    found = true
                    deletedWidgetList.remove(pWidget)
                    break
                } else if (lWidget.id == pWidget.id) {
                    found = true
                    deletedWidgetList.remove(pWidget)
                    updatedWidgetList.add(Pair(pWidget, lWidget))
                    break
                }
            }
            if (!found) {
                addedWidgetList.add(lWidget)
            }
        }

        /** handle Added Widgets */
        for (item in addedWidgetList) {
            Log.d(TAG, "Widget added.(${item})")
            for (pInfo in installedWidgetList) {
                if (pInfo.provider.className == item.className) {
                    val loader = WidgetHostViewLoader(this, pInfo, item)
                    /** After loading WidgetView onWidgetViewLoaded is called */
                    loader.loadWidgetView()
                    break
                }
            }
        }

        /** handle Deleted Widgets */
        for (item in deletedWidgetList) {
            Log.d(TAG, "Widget deleted.(${item})")
            containerConnector.deleteWidget(item)
        }

        /** handle Updated Widget */
        for ((pItem, lItem) in updatedWidgetList) {
            Log.d(TAG, "Widget updated.(From(${pItem}), To(${lItem}))")
            containerConnector.updateWidget(pItem, lItem)
        }

        currentWidgetList = latestWidgetList.toList()
    }

    fun onWidgetViewLoaded(hostView: AppWidgetHostView, pInfo: HomeAppWidgetProviderInfo, item: WidgetItemInfo) {
        val widgetViewCell = layoutInflater.inflate(R.layout.widget_view_cell, shrinkTable, false) as WidgetViewCell
        widgetViewCell.spanX = pInfo.spanX
        widgetViewCell.spanY = pInfo.spanY
        widgetViewCell.item = item
        widgetViewCell.setBackgroundColor(Color.YELLOW)
        widgetViewCell.addWidgetView(hostView)
        item.appWidgetId = hostView.appWidgetId
        homeModeChangeListeners[hostView.appWidgetId] = widgetViewCell
        containerConnector.addWidget(widgetViewCell)
    }

    fun requestAppWidgetBind(appWidgetId: Int, pInfo: HomeAppWidgetProviderInfo, loader: WidgetHostViewLoader) {
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, pInfo.provider)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER_PROFILE, pInfo.profile)

        pendingWidgets[appWidgetId] = Pair(pInfo, loader)
        startActivityForResult(intent, REQUEST_BIND_APPWIDGET)
    }

    fun transitMode(nextMode: HomeMode) {
        when (nextMode) {
            HomeMode.DISPLAY -> {
                shrinkTable.visibility = View.VISIBLE
                widgetSelectionView.visibility = View.GONE
                backButton.visibility = View.GONE
                customBottons.visibility = View.GONE

                shrinkTable.unShrink()
            }
            HomeMode.REARRANGEMENT -> {
                shrinkTable.visibility = View.VISIBLE
                widgetSelectionView.visibility = View.GONE
                backButton.visibility = View.VISIBLE
                customBottons.visibility = View.VISIBLE

                shrinkTable.shrink()
            }
            HomeMode.SELECTION -> {
                shrinkTable.visibility = View.GONE
                widgetSelectionView.visibility = View.VISIBLE
                backButton.visibility = View.VISIBLE
                customBottons.visibility = View.GONE
            }
        }
        notifyListeners(nextMode)
        mode = nextMode
    }

    private fun notifyListeners(mode: HomeMode) {
        for ((id, cell) in homeModeChangeListeners) {
            cell.onHomeModeChanged(mode)
        }
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
