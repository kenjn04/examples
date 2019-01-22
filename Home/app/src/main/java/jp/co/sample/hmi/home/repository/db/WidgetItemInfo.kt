package jp.co.sample.hmi.home.repository.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import jp.co.sample.hmi.home.common.WidgetIdProvider

@Entity(tableName = "widgets")
data class WidgetItemInfo (
    @PrimaryKey var id: Int,
    var packageName: String,
    var className: String,
    var containerId: Int,
    var coordinateX: Int,
    var coordinateY: Int,
    @Ignore var appWidgetId: Int? = null
) {
    // For WidgetAddCell. Id 0 is assigned to WidgetAddCell
    constructor() : this(0, "", "", 0, 0, 0)

    // For WidgetViewCell
    @Ignore
    constructor(
        packageName: String,
        className: String,
        containerId: Int,
        coordinateX: Int,
        coordinateY: Int
    ): this(
        WidgetIdProvider.getInstance().getId(),
        packageName,
        className,
        containerId,
        coordinateX,
        coordinateY
    )

    // For WidgetViewCell
    @Ignore
    constructor(item: WidgetItemInfo) : this(
        item.id,
        item.packageName,
        item.className,
        item.containerId,
        item.coordinateX,
        item.coordinateY,
        item.appWidgetId
    )

    override fun toString(): String {
        return if (appWidgetId == null) {
            "id=${id}, package=${packageName}, class=${className}, " +
            "containerId=${containerId}, coorinateX=${coordinateX}, coorinateY=${coordinateY}"
        } else {
            "id=${id}, package=${packageName}, class=${className}, " +
            "containerId=${containerId}, coorinateX=${coordinateX}, coorinateY=${coordinateY}, " +
            "appWidgetId=${appWidgetId}"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is WidgetItemInfo) {
            if (
                (id == other.id) and
                (packageName == other.packageName) and
                (className == other.className) and
                (containerId == other.containerId) and
                (coordinateX == other.coordinateX) and
                (coordinateY == other.coordinateY)
            ) {
                return true
            }
        }
        return false
    }
}