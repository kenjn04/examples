package jp.co.sample.hmi.home.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.repository.db.WidgetItemInfo

interface HomeRepository{

    val currentWidgets: LiveData<List<WidgetItemInfo>>

    fun getInstalledWidgetList(): MutableList<HomeAppWidgetProviderInfo>

    fun addWidget(item: WidgetItemInfo)

    fun deleteWidget(item: WidgetItemInfo)
}
