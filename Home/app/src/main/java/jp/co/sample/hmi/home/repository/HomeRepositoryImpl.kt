package jp.co.sample.hmi.home.repository

import android.app.Application
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.arch.lifecycle.LiveData
import android.os.UserManager
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.repository.db.WidgetDao
import jp.co.sample.hmi.home.repository.db.WidgetDatabase
import jp.co.sample.hmi.home.repository.db.WidgetItemInfo
import android.os.AsyncTask



class HomeRepositoryImpl private constructor(
        application: Application,
        private val appWidgetManager: AppWidgetManager,
        private val userManager: UserManager
) : HomeRepository {

    private val widgetDao: WidgetDao

    override val currentWidgets: LiveData<List<WidgetItemInfo>>

    init {
        val db = WidgetDatabase.getDatabase(application)
        widgetDao = db.widgetDao()
        currentWidgets = widgetDao.getAll()
    }

    override fun getInstalledWidgetList(): MutableList<HomeAppWidgetProviderInfo> {
        // get all widgets as WidgetItem
        val installedWidgetList = mutableListOf<HomeAppWidgetProviderInfo>()
        for (widgetInfo in getWidgetInfoFromAllProviders()) {
            installedWidgetList.add(HomeAppWidgetProviderInfo.fromProviderInfo(widgetInfo))
        }
        return installedWidgetList
    }

    private fun getWidgetInfoFromAllProviders(): MutableList<AppWidgetProviderInfo> {
        val providers = mutableListOf<AppWidgetProviderInfo>()
        for (user in userManager.userProfiles) {
            providers.addAll(appWidgetManager.getInstalledProvidersForProfile(user))
        }
        return providers
    }

    override fun addWidget(item: WidgetItemInfo) {
        AsyncInsertTask(widgetDao).execute(item)
    }

    override fun deleteWidget(item: WidgetItemInfo) {
        AsyncDeleteTask(widgetDao).execute(item)
    }

    /** Operation to database should be asynchronous */
    private class AsyncInsertTask internal constructor(val dao: WidgetDao): AsyncTask<WidgetItemInfo, Unit, Unit>()
    {
        override fun doInBackground(vararg params: WidgetItemInfo?) = dao.insert(params[0]!!)
    }

    /** Operation to database should be asynchronous */
    private class AsyncDeleteTask internal constructor(private val dao: WidgetDao) : AsyncTask<WidgetItemInfo, Unit, Unit>()
    {
        override fun doInBackground(vararg params: WidgetItemInfo?) = dao.delete(params[0]!!)
    }

    companion object {

        @Volatile
        private var INSTANCE: HomeRepositoryImpl? = null
        fun getInstance(application: Application, appWidgetManager: AppWidgetManager, userManager: UserManager): HomeRepository {
            if (INSTANCE == null) {
                synchronized(HomeRepositoryImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = HomeRepositoryImpl(application, appWidgetManager, userManager)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
