package jp.co.sample.hmi.home.repository

import android.arch.lifecycle.MutableLiveData
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.common.WidgetItemInfo

interface HomeRepository{

    fun getInstalledWidgetList(): MutableList<HomeAppWidgetProviderInfo>

    fun getCurrentWidgets(): MutableLiveData<List<WidgetItemInfo>>

}
