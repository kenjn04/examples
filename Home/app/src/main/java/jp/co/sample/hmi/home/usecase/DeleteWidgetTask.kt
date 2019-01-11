package jp.co.sample.hmi.home.usecase

import android.content.ComponentName
import jp.co.sample.hmi.home.repository.HomeRepository

class DeleteWidgetTask(
        private val homeRepository: HomeRepository
) {

    fun execute(componentName: ComponentName) = homeRepository.deleteWidget(componentName)
}
