package jp.co.sample.hmi.home.view.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.view.Gravity
import android.view.View
import jp.co.sample.hmi.home.view.HomeActivity

class WidgetContainerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int,
    private val widgetContainerId: Int = 0
): FrameLayout(context, attrs, defStyle) {

    private val home: HomeActivity = context as HomeActivity

    lateinit var containerConnector: WidgetContainerConnector

    private val params = home.params

    /** Parameters */
    private val WIDGET_REARRANGE_ANIMATION_DURATION_MS = 500L

    private val numX: Int
    private val numY: Int
    private val widgetFrameWidth: Int
    private val widgetFrameHeight: Int

    var relativeTranslationX: Float = 0F

    private lateinit var widgetPositionX: MutableList<Float>
    private lateinit var widgetPositionY: MutableList<Float>

    /** */
    private var draggingWidget: WidgetCell? = null

    private val shadowFrame: FrameLayout = FrameLayout(context)
    private var shadowX: Int = -1
    private var shadowY: Int = -1

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, id: Int) : this(context, null, 0, id)

    init {
        // set parameters
        numX = params.widgetNumInContainerX
        numY = params.widgetNumInContainerY
        widgetFrameWidth = params.widgetFrameWidth
        widgetFrameHeight = params.widgetFrameHeight

        // set shadow frame
        shadowFrame.visibility = View.GONE
        shadowFrame.setBackgroundColor(Color.CYAN)
        addView(shadowFrame)

        initializeWidgetPosition()
    }

    private fun initializeWidgetPosition() {
        var positionX = (params.displaySize.x - params.widgetNumInContainerX * params.widgetFrameWidth).toFloat() / 2
        var positionY = (params.displaySize.y - params.widgetNumInContainerY * params.widgetFrameHeight).toFloat() / 2
        widgetPositionX = mutableListOf()
        widgetPositionY = mutableListOf()
        for (x in 1..numX) {
            widgetPositionX.add(positionX)
            positionX += params.widgetFrameWidth.toFloat()
        }
        for (y in 0..(numY - 1)) {
            widgetPositionY.add(positionY)
            positionY += params.widgetFrameHeight.toFloat()
        }
    }

    fun initialize(connector: WidgetContainerConnector) {
        relativeTranslationX = translationX + connector.relativeTranslationX
        containerConnector = connector
    }

    fun addWidget(widget: WidgetCell, x: Int, y: Int) {

        val width = widget.spanX * widgetFrameWidth
        val height = widget.spanY * widgetFrameHeight
        val params = widget.layoutParams

        var layoutParams: FrameLayout.LayoutParams = if (params != null)
            {
                FrameLayout.LayoutParams(params).apply {
                    this.width = width
                    this.height = height
                }
            } else {
                FrameLayout.LayoutParams(width, height)
            }
        layoutParams.gravity = Gravity.TOP or Gravity.LEFT
        widget.apply {
            this.layoutParams = layoutParams

            containerId = widgetContainerId
            positionX = x
            positionY = y

            translationX = widgetPositionX[x]
            translationY = widgetPositionY[y]

            widgetContainerView = this@WidgetContainerView
        }
        Log.d("aaabbbccc", "" + widget.item.className + " " + widget.item.containerId)
        addView(widget)
    }

    fun removeWidget(widget: WidgetCell) {
        removeView(widget)
    }

    private fun enableShadowFrame() {

        shadowFrame.layoutParams.apply {
            width = draggingWidget!!.spanX * widgetFrameWidth
            height = draggingWidget!!.spanY * widgetFrameHeight
        }
        shadowFrame.visibility = View.VISIBLE
    }

    fun calculateDraggingWidgetPosition(): Pair<Int, Int> {

        if (shadowFrame.visibility == View.VISIBLE) {
            shadowFrame.visibility = View.GONE
        }

        val widget = draggingWidget!!
        val positionLeft = widget.translationX - relativeTranslationX
        val positionRight = positionLeft + widgetFrameWidth * widget.spanX
        val positionTop = widget.translationY
        val positionBottom = positionTop + widgetFrameHeight * widget.spanY

        var toX: Int = -1
        var toY: Int = -1
        var maxSize: Float = 0F
        for (x in 0..(numX - widget.spanX)) {
            for (y in 0..(numY - widget.spanY)) {
                val shadowLeft = widgetPositionX[x]
                val shadowRight = shadowLeft + widgetFrameWidth * widget.spanX
                val shadowTop = widgetPositionY[y]
                val shadowBottom = shadowTop + widgetFrameHeight * widget.spanY

                var width: Float = 0F
                var height: Float = 0F
                if ((shadowLeft <= positionLeft) and (positionLeft < shadowRight)) {
                    width = shadowRight - positionLeft
                } else if ((positionLeft <= shadowLeft) and (shadowLeft < positionRight)) {
                    width = positionRight - shadowLeft
                }
                if ((shadowTop <= positionTop) and (positionTop < shadowBottom)) {
                    height = shadowBottom - positionTop
                } else if ((positionTop <= shadowTop) and (shadowTop < positionBottom)) {
                    height = positionBottom - shadowTop
                }
                val size = width * height
                if (size > maxSize) {
                    maxSize = size
                    toX = x
                    toY = y
                }
            }
        }
        return Pair(toX, toY)
    }

    fun showShadowFrame(x: Int, y: Int) {

        if (shadowFrame.visibility != View.VISIBLE) {
            enableShadowFrame()
        }
        shadowFrame.apply {
            translationX = widgetPositionX[x]
            translationY = widgetPositionY[y]
        }
        shadowX = x
        shadowY = y
    }

    fun startWidgetDrag(widget: WidgetCell) {
        draggingWidget = widget
        removeView(shadowFrame)
        addView(shadowFrame)
    }

    fun finishWidgetDrag(drop: Boolean) {

        val draggingWidget = draggingWidget!!
        this.draggingWidget = null

        shadowFrame.visibility = View.GONE

        /*
        if (drop) {
            home.shrinkTable.removeView(draggingWidget)
            addWidget(draggingWidget, shadowX, shadowY)
        }
        */
    }
}