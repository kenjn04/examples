package jp.co.sample.hmi.home.usecase

import android.arch.lifecycle.LiveData
import jp.co.sample.hmi.home.repository.db.WidgetItemInfo
import jp.co.sample.hmi.home.repository.HomeRepository

class GetCurrentWidgetsTask(
        private val homeRepository: HomeRepository
) {

    fun execute(): LiveData<List<WidgetItemInfo>> = homeRepository.currentWidgets
}
