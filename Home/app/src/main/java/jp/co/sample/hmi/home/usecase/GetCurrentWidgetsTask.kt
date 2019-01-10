package jp.co.sample.hmi.home.usecase

import android.arch.lifecycle.MutableLiveData
import jp.co.sample.hmi.home.common.WidgetItemInfo
import jp.co.sample.hmi.home.repository.HomeRepository

class GetCurrentWidgetsTask(
        private val homeRepository: HomeRepository
) {

    fun execute(): MutableLiveData<List<WidgetItemInfo>> {
        val currentWidgets= homeRepository.getCurrentWidgets()

        return currentWidgets
    }
}
