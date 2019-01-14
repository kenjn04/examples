package jp.co.sample.hmi.home.usecase

import jp.co.sample.hmi.home.repository.HomeRepository
import jp.co.sample.hmi.home.repository.db.WidgetItemInfo

class DeleteWidgetTask(
        private val homeRepository: HomeRepository
) {

    fun execute(item: WidgetItemInfo) = homeRepository.deleteWidget(item)
}
