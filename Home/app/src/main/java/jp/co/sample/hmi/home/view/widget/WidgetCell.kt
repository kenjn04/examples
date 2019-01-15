package jp.co.sample.hmi.home.view.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

abstract class WidgetCell(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    lateinit var widgetContainerView: WidgetContainerView

    abstract var spanX: Int
    abstract var spanY: Int
    abstract var positionX: Int
    abstract var positionY: Int
}