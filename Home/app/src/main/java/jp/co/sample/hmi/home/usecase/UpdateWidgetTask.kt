package jp.co.sample.hmi.home.usecase

import jp.co.sample.hmi.home.repository.HomeRepository
import jp.co.sample.hmi.home.repository.db.WidgetItemInfo

class UpdateWidgetTask(
        private val homeRepository: HomeRepository
) {

    fun execute(items: List<WidgetItemInfo>) = homeRepository.updateWidget(items)
}
