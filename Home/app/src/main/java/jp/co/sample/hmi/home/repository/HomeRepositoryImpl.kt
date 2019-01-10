package jp.co.sample.hmi.home.repository

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.os.UserManager
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.common.WidgetItemInfo

class HomeRepositoryImpl private constructor(
        private val appWidgetManager: AppWidgetManager,
        private val userManager: UserManager
) : HomeRepository {

    private val currentWidgets = MutableLiveData<List<WidgetItemInfo>>()

    init {
        val pName = "com.android.chrome"
        val cName = "org.chromium.chrome.browser.searchwidget.SearchWidgetProvider"
        val widget = WidgetItemInfo(
                ComponentName(pName, cName),
                0,
                0,
                0
        )
        val list = listOf<WidgetItemInfo>(widget)
        currentWidgets.postValue(list)
    }

    override fun getInstalledWidgetList(): MutableList<HomeAppWidgetProviderInfo> {
        // get all widgets as WidgetItem
        val installedWidgetList = mutableListOf<HomeAppWidgetProviderInfo>()
        for (widgetInfo in getWidgetInfoFromAllProviders()) {
            installedWidgetList.add(HomeAppWidgetProviderInfo.fromProviderInfo(widgetInfo))
        }
        return installedWidgetList
    }

    override fun getCurrentWidgets(): MutableLiveData<List<WidgetItemInfo>> = currentWidgets

    private fun getWidgetInfoFromAllProviders(): MutableList<AppWidgetProviderInfo> {
        val providers = mutableListOf<AppWidgetProviderInfo>()
        for (user in userManager.userProfiles) {
            providers.addAll(appWidgetManager.getInstalledProvidersForProfile(user))
        }
        return providers
    }

    companion object {

        @Volatile
        private var INSTANCE: HomeRepositoryImpl? = null
        fun getInstance(appWidgetManager: AppWidgetManager, userManager: UserManager): HomeRepository {
            if (INSTANCE == null) {
                synchronized(HomeRepositoryImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = HomeRepositoryImpl(appWidgetManager, userManager)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
