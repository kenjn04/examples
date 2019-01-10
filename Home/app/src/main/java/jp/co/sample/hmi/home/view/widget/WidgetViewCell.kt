package jp.co.sample.hmi.home.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import jp.co.sample.hmi.home.util.DraggingHelper
import jp.co.sample.hmi.home.view.HomeActivity
import jp.co.sample.hmi.home.view.HomeMode

class WidgetViewCell(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): FrameLayout(context, attrs, defStyle), View.OnLongClickListener {

    private val home: HomeActivity = context as HomeActivity

    lateinit var widgetContainerView: WidgetContainerView

    var spanX: Int = 1
    var spanY: Int = 1
    var positionX: Int = 1
    var positionY: Int = 1
    var appWidgetId: Int = -1

    private val draggingHelper = DraggingHelper(this)

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, spanX: Int, spanY: Int, id: Int) : this(context, null, 0) {
        this.spanX = spanX
        this.spanY = spanY
        this.appWidgetId = id
    }

    init {
        setOnLongClickListener(this)
    }

    private var dragAllowed = false

    override fun onLongClick(v: View?): Boolean {
        when (home.mode) {
            HomeMode.DISPLAY -> {
                home.transitMode(HomeMode.REARRANGE)
            }
            HomeMode.REARRANGE -> {
                startDragging()
                dragAllowed = true
            }
        }
        return true
    }

    private fun startDragging() {
        widgetContainerView.removeWidget(this)
        home.workspace.addView(this)
        translationX += widgetContainerView.relativeTranslationX
        translationY += widgetContainerView.translationY

        home.workspace.startWidgetDragging(this)
    }

    fun finishDragging() {
        home.workspace.finishWidgetDragging()
        dragAllowed = false
    }

    fun onDragging(x: Float, y: Float) {
        if (dragAllowed and !draggingHelper.dragStarted) {
            draggingHelper.startDragging(x, y)
        } else {
            draggingHelper.movePositionByDrag(x, y, false)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // disable widget touch
        if (home.mode == HomeMode.REARRANGE) {
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other is WidgetViewCell) {
            if (appWidgetId.equals(other.appWidgetId)) return true
        }
        return false
    }
}