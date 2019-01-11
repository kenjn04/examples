package jp.co.sample.hmi.home.view

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.R
import jp.co.sample.hmi.home.common.WidgetItemInfo
import jp.co.sample.hmi.home.util.WidgetHostViewLoader
import jp.co.sample.hmi.home.view.preview.WidgetSelectionView
import jp.co.sample.hmi.home.view.widget.WidgetContainerConnector
import jp.co.sample.hmi.home.view.widget.WidgetViewCell
import jp.co.sample.hmi.home.view.widget.Workspace
import jp.co.sample.hmi.home.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {

    private val APP_WIDGET_HOST_ID = 12345

    private val homeViewModel: HomeViewModel by viewModel()

    private var installedWidgetList: List<HomeAppWidgetProviderInfo> = listOf()

    lateinit var appWidgetHost: AppWidgetHost

    private val pendingWidgets
            = mutableMapOf<Int, Pair<HomeAppWidgetProviderInfo, WidgetHostViewLoader>>()

    /** Views */
    lateinit var workspace: Workspace
    private lateinit var widgetSelectionView: WidgetSelectionView
    private lateinit var containerConnector: WidgetContainerConnector

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
        workspace = findViewById(R.id.workspace)
        widgetSelectionView = findViewById(R.id.widget_preview)
        containerConnector = findViewById(R.id.widget_container_connector)

        transitMode(mode)

        // TODO: This is temporary. Button for adding widget should be in WidgetContainerView
        tmp_button.setOnClickListener {
            transitMode(HomeMode.SELECTION)
        }
    }

    // TODO: Maybe need to be called when application is added and removed to get latest lists
    private fun updateInstalledWidgetList() {
        installedWidgetList = homeViewModel.installedWidgetList
        widgetSelectionView.setWidgets(installedWidgetList)
    }

    fun addWidget(componentName: ComponentName) {
        // TODO: Need to get layout information from workspace
        val item = WidgetItemInfo(componentName, 0,2,0)
        /** After adding, the updateCurrentWidgets will be called by LiveData */
        homeViewModel.addWidget(item)
        transitMode(HomeMode.DISPLAY)
    }

    fun deleteWidget(componentName: ComponentName) {
        // TODO: appWidgetHost.deleteAppWidgetId is required
        homeViewModel.deleteWidget(componentName)
    }

    private fun setCurrentWidgets() {
        val currentWidgetsLiveData = homeViewModel.currentWidgets
        val currentWidgets = currentWidgetsLiveData.value
        updateCurrentWidgets(currentWidgets)
        currentWidgetsLiveData.observeForever {
            updateCurrentWidgets(it)
        }
    }

    private fun updateCurrentWidgets(currentWidgets: List<WidgetItemInfo>?) {
        if (currentWidgets == null) return
        val widgets = currentWidgets!!
        for (widget in widgets) {
            for (widgetInfo in installedWidgetList) {
                if (widgetInfo.provider == widget.componentName) {
                    val loader = WidgetHostViewLoader(this, widgetInfo, widget.containerId, widget.coordinateX, widget.coordinateY)
                    // After loadin WidgetView onWidgetViewLoaded is called
                    loader.loadWidgetView()
                    break
                }
            }
        }
    }

    fun onWidgetViewLoaded(hostView: AppWidgetHostView, containerId: Int, coordinateX: Int, coordinateY: Int, spanX: Int, spanY: Int) {
        val widgetViewCell = layoutInflater.inflate(R.layout.widget_view_cell, workspace, false) as WidgetViewCell
        widgetViewCell.spanX = spanX
        widgetViewCell.spanY = spanY
        widgetViewCell.setBackgroundColor(Color.YELLOW)
        widgetViewCell.addView(hostView)
        containerConnector.addWidget(widgetViewCell, containerId, coordinateX, coordinateY)
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
                workspace.visibility = View.VISIBLE
                widgetSelectionView.visibility = View.GONE

                workspace.unShrink()
            }
            HomeMode.REARRANGEMENT -> {
                workspace.visibility = View.VISIBLE
                widgetSelectionView.visibility = View.GONE

                workspace.shrink()
            }
            HomeMode.SELECTION -> {
                workspace.visibility = View.GONE
                widgetSelectionView.visibility = View.VISIBLE
            }
        }
        mode = nextMode
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
