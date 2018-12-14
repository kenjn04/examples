package jp.co.nissan.hmi.myapplication

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import jp.co.nissan.hmi.myapplication.common.WidgetItem
import org.w3c.dom.Text

class WidgetCell: LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    lateinit var widgetImage: ImageView
    lateinit var widgetName: TextView
    lateinit var widgetDims: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        widgetImage = findViewById(R.id.widget_preview)
        widgetName = findViewById(R.id.widget_name)
        widgetDims = findViewById(R.id.widget_dims)
    }

    var widgetItem: WidgetItem? = null

    fun applyFromCellItem(item: WidgetItem) {
        widgetItem = item
    }

    fun ensurePreview() {
        // TODO: Is this correct?
        val density = this.resources.displayMetrics.density
        // TODO: Is this heavy task? Should be done in background? caching is required?
        val preview = widgetItem!!.widgetInfo.loadPreviewImage(this.context, density.toInt())
        widgetImage.setImageDrawable(preview)
    }

}