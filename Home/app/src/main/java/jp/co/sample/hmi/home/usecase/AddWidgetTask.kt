package jp.co.sample.hmi.home.usecase

import jp.co.sample.hmi.home.repository.db.WidgetItemInfo
import jp.co.sample.hmi.home.repository.HomeRepository

class AddWidgetTask(
        private val homeRepository: HomeRepository
) {

    fun execute(item: WidgetItemInfo) = homeRepository.addWidget(item)
}
