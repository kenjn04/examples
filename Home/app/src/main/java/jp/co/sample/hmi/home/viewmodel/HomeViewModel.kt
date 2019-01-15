package jp.co.sample.hmi.home.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.repository.db.WidgetItemInfo
import jp.co.sample.hmi.home.usecase.*

class HomeViewModel(
    application: Application,
    private val getInstalledWidgetListTask: GetInstalledWidgetListTask,
    private val getCurrentWidgetsTask: GetCurrentWidgetsTask,
    private val addWidgetTask: AddWidgetTask,
    private val deleteWidgetTask: DeleteWidgetTask,
    private val updateWidgetTask: UpdateWidgetTask
) : AndroidViewModel(application) {

    val installedWidgetList: List<HomeAppWidgetProviderInfo>
        get() {
            return getInstalledWidgetListTask.execute()
        }

    val currentWidgets: LiveData<List<WidgetItemInfo>>
        get() {
            return getCurrentWidgetsTask.execute()
        }

    fun addWidget(item: WidgetItemInfo) = addWidgetTask.execute(item)

    fun deleteWidget(item: WidgetItemInfo) = deleteWidgetTask.execute(item)

    fun updateWidget(items: List<WidgetItemInfo>) = updateWidgetTask.execute(items)
}
