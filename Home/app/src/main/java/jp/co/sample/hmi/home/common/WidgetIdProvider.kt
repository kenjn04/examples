package jp.co.sample.hmi.home.common

import android.arch.lifecycle.LiveData
import jp.co.sample.hmi.home.repository.db.WidgetItemInfo

/**
 * This is used for assigning id for WidgetItemInfo
 */
class WidgetIdProvider private constructor(
    currentWidgetItems: LiveData<List<WidgetItemInfo>>
) {

    private var id: Int = 1

    private var usedId = mutableListOf<Int>()

    init {
        currentWidgetItems.observeForever {
            usedId.clear()
            if (it == null) return@observeForever
            for (item in it) {
                usedId.add(item.id)
            }
        }
    }

    fun getId(): Int {
        for (i in 0..(Integer.MAX_VALUE - 1)) {
            id = (id + i) % Integer.MAX_VALUE
            if (id == 0) continue
            if (!usedId.contains(id)) {
                usedId.add(id)
                return id++
            }
        }
        return Integer.MAX_VALUE
    }

    companion object {

        @Volatile
        private var INSTANCE: WidgetIdProvider? = null

        fun getInstance(): WidgetIdProvider = INSTANCE!!

        fun initialize(currentWidgetItems: LiveData<List<WidgetItemInfo>>) {
            if (INSTANCE == null) {
                synchronized(WidgetIdProvider::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = WidgetIdProvider(currentWidgetItems)
                    }
                }
            }
        }
    }
}
