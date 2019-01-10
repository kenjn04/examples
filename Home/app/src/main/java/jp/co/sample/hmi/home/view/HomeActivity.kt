package jp.co.sample.hmi.home.view

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.UserManager
import android.view.View
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.R
import jp.co.sample.hmi.home.common.WidgetItemInfo
import jp.co.sample.hmi.home.repository.HomeRepositoryImpl
import jp.co.sample.hmi.home.usecase.GetCurrentWidgetsTask
import jp.co.sample.hmi.home.usecase.GetInstalledWidgetListTask
import jp.co.sample.hmi.home.view.widget.WidgetHostViewLoader
import jp.co.sample.hmi.home.view.widget.Workspace
import jp.co.sample.hmi.home.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {

    private val APP_WIDGET_HOST_ID = 12345

//    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var homeViewModel: HomeViewModel

    private var installedWidgetList: List<HomeAppWidgetProviderInfo> = listOf()

    lateinit var appWidgetHost: AppWidgetHost

    private val pendingWidgets
            = mutableMapOf<Int, Pair<HomeAppWidgetProviderInfo, WidgetHostViewLoader>>()

    /** Views */
    lateinit var workspace: Workspace
//    private lateinit var widgetPreview: WidgetPreviews

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

        // TODO: remove the below by checking Koin di
        /** kokokara */
        val repo = HomeRepositoryImpl.getInstance(
                        AppWidgetManager.getInstance(this),
                        getSystemService(Context.USER_SERVICE) as UserManager
        )
        homeViewModel = HomeViewModel(
                application,
                GetInstalledWidgetListTask(repo),
                GetCurrentWidgetsTask(repo)
        )
        /** kokomade */

        setViews()

        updateInstalledWidgetList()
        setCurrentWidgets()
    }

    private fun setViews() {

    }

    // TODO: Maybe need to be called when application is added and removed to get latest lists
    private fun updateInstalledWidgetList() {
        installedWidgetList = homeViewModel.installedWidgetList
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

    // TODO: Update is required.
    fun onWidgetViewLoaded(hostView: AppWidgetHostView, containerId: Int, coordinateX: Int, coordinateY: Int) {
/*
        val widgetViewCell = WidgetViewCell(hostView)
        widgetViewCell.setBackgroundColor(Color.YELLOW)
        containerConnector.addWidget(widgetViewCell, containerId, coordinateX, coordinateY)
*/
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
//                widgetPreview.visibility = View.GONE

                workspace.unShrink()
            }
            HomeMode.REARRANGE -> {
                workspace.visibility = View.VISIBLE
//                widgetPreview.visibility = View.GONE

                workspace.shrink()
            }
            HomeMode.SELECT -> {
                workspace.visibility = View.GONE
//                widgetPreview.visibility = View.VISIBLE
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
