package jp.co.nissan.hmi.myapplication.widgetselection

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import jp.co.nissan.hmi.myapplication.R
import jp.co.nissan.hmi.myapplication.common.PendingAppWidgetInfo
import jp.co.nissan.hmi.myapplication.common.WidgetItem

class WidgetCell(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
) : LinearLayout(context, attrs, defStyle) {

    lateinit var item: WidgetItem
    lateinit var image: ImageView
    lateinit var name: TextView
    lateinit var dims: TextView

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onFinishInflate() {
        super.onFinishInflate()
        image = findViewById(R.id.widget_preview)
        name = findViewById(R.id.widget_name)
        dims = findViewById(R.id.widget_dims)
    }

    fun applyFromCellItem(item: WidgetItem) {
        this.item = item
        name.text = item.label
        dims.text = context.getString(R.string.widget_dims_format, item.spanX, item.spanY)
        // TODO; Is this required?
        dims.contentDescription = context.getString(R.string.widget_accessible_dims_format, item.spanX, item.spanY)

        tag = PendingAppWidgetInfo(item.widgetInfo)
    }

    fun ensurePreview() {
        // TODO: Is this correct?
        val density = this.resources.displayMetrics.density
        // TODO: Is this heavy task? Should be done in background? caching is required?
        val preview = item!!.widgetInfo.loadPreviewImage(this.context, density.toInt())
        image.setImageDrawable(preview)
//        val preview = generateWidgetPreview(item!!.widgetInfo)
//        image.setImageBitmap(preview)
    }

    // From widgetPreviewLoader in launcher3
/*
    private fun generateWidgetPreview(info: LauncherAppWidgetProviderInfo): Bitmap {
        val drawable = info.loadPreviewImage(this.context, 0)
    }
*/
}