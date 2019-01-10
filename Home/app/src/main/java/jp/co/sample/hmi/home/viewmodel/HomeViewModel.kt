package jp.co.sample.hmi.home.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.common.WidgetItemInfo
import jp.co.sample.hmi.home.usecase.GetCurrentWidgetsTask

import jp.co.sample.hmi.home.usecase.GetInstalledWidgetListTask

class HomeViewModel(
    application: Application,
    private val getInstalledWidgetListTask: GetInstalledWidgetListTask,
    private val getCurrentWidgetsTask: GetCurrentWidgetsTask
) : AndroidViewModel(application) {

    val installedWidgetList: List<HomeAppWidgetProviderInfo>
        get() {
            return getInstalledWidgetListTask.execute()
        }

    val currentWidgets: MutableLiveData<List<WidgetItemInfo>>
        get() {
            return getCurrentWidgetsTask.execute()
        }
}
