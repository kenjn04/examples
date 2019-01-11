package jp.co.sample.hmi.home.repository

import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.common.WidgetItemInfo

interface HomeRepository{

    fun getInstalledWidgetList(): MutableList<HomeAppWidgetProviderInfo>

    fun getCurrentWidgets(): MutableLiveData<List<WidgetItemInfo>>

    fun addWidget(item: WidgetItemInfo)

    fun deleteWidget(componentName: ComponentName)
}
