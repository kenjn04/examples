package jp.co.sample.hmi.home.view.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import jp.co.sample.hmi.home.repository.db.WidgetItemInfo

abstract class WidgetCell(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    lateinit var widgetContainerView: WidgetContainerView

    var item: WidgetItemInfo = WidgetItemInfo()

    var spanX: Int = 1
    var spanY: Int = 1

    var positionX: Int
        set(value) {
            item.coordinateX = value
        }
        get() = item.coordinateX

    var positionY: Int
        set(value) {
            item.coordinateY = value
        }
        get() = item.coordinateY

}