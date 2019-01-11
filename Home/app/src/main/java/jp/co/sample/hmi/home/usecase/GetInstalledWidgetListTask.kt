package jp.co.sample.hmi.home.usecase

import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo
import jp.co.sample.hmi.home.repository.HomeRepository

class GetInstalledWidgetListTask(
        private val homeRepository: HomeRepository
) {

    fun execute(): List<HomeAppWidgetProviderInfo> {
        val installedWidgetList = homeRepository.getInstalledWidgetList()

        return installedWidgetList.toList()
    }
}
