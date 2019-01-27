package jp.co.sample.hmi.home.view.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.view.MotionEvent
import jp.co.sample.hmi.home.view.HomeActivity
import jp.co.sample.hmi.home.view.HomeMode

class WidgetBaseView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle) {

    private val home: HomeActivity = context as HomeActivity

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (home.mode == HomeMode.REARRANGEMENT) {
            return true
        }
        return false
    }
}