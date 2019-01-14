package jp.co.sample.hmi.home.repository.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.ComponentName

@Entity(tableName = "widgets")
data class WidgetItemInfo(
//        @PrimaryKey var componentName: ComponentName,
        var packageName: String,
        @PrimaryKey var className: String,
        var containerId: Int,
        var coordinateX: Int,
        var coordinateY: Int
)