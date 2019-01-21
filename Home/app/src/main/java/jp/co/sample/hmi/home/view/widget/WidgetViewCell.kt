package jp.co.sample.hmi.home.view.widget

import android.appwidget.AppWidgetHostView
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import jp.co.sample.hmi.home.R
import jp.co.sample.hmi.home.util.DraggingHelper
import jp.co.sample.hmi.home.view.HomeActivity
import jp.co.sample.hmi.home.view.HomeMode
import jp.co.sample.hmi.home.view.HomeModeChangeListener

class WidgetViewCell(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): WidgetCell(context, attrs, defStyle), View.OnLongClickListener, HomeModeChangeListener {

    private val home: HomeActivity = context as HomeActivity

    lateinit var widgetView: FrameLayout
    lateinit var deleteButton: Button

    private val draggingHelper = DraggingHelper(this)

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        setOnLongClickListener(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        widgetView = findViewById(R.id.widget_view)
        deleteButton = findViewById(R.id.delete_button)
        deleteButton.setOnClickListener {
            home.deleteWidget(item)
        }
        deleteButton.visibility = View.GONE
    }

    fun addWidgetView(hostView: AppWidgetHostView) {
        widgetView.addView(hostView)
    }

    fun revertPosition() {
        widgetContainerView.addWidget(this, positionX, positionY)
    }

    fun deleteFromContainer() {
        widgetContainerView.removeWidget(this)
    }

    private var dragAllowed = false

    override fun onLongClick(v: View?): Boolean {
        when (home.mode) {
            HomeMode.DISPLAY -> {
                home.transitMode(HomeMode.REARRANGEMENT)
            }
            HomeMode.REARRANGEMENT -> {
                startDragging()
                dragAllowed = true
            }
            HomeMode.SELECTION -> {
                // Never reach here
            }
        }
        return true
    }

    override fun onHomeModeChanged(mode: HomeMode) {
        when (mode) {
            HomeMode.DISPLAY -> {
                deleteButton.visibility = View.GONE
            }
            HomeMode.REARRANGEMENT -> {
                deleteButton.visibility = View.VISIBLE
            }
            HomeMode.SELECTION -> {
                // Nothing to do
            }
        }
    }

    private fun startDragging() {
        widgetContainerView.removeWidget(this)
        home.shrinkTable.addView(this)
        translationX += widgetContainerView.relativeTranslationX
        translationY += widgetContainerView.translationY

        home.shrinkTable.startWidgetDragging(this)
    }

    fun finishDragging() {
        home.shrinkTable.finishWidgetDragging()
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
        // TODO: disable widget touch
        if (home.mode == HomeMode.REARRANGEMENT) {
            return false
        }
        return false
    }
}