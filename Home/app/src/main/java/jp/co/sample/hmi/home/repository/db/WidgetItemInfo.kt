package jp.co.sample.hmi.home.repository.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "widgets")
data class WidgetItemInfo(
//        @PrimaryKey var componentName: ComponentName,
    var packageName: String,
    @PrimaryKey var className: String,
    var containerId: Int,
    var coordinateX: Int,
    var coordinateY: Int,
    @Ignore var appWidgetId: Int? = null
) {
    constructor() : this("", "", 0, 0, 0)
    constructor(item: WidgetItemInfo) : this(
            item.packageName,
            item.className,
            item.containerId,
            item.coordinateX,
            item.coordinateY
    )
}