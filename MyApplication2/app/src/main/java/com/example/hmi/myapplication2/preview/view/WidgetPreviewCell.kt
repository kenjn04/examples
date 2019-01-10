package com.example.hmi.myapplication2.preview.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.hmi.myapplication2.R
import com.example.hmi.myapplication2.preview.PendingAppWidgetInfo

class WidgetPreviewCell(
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

        val drawable = item!!.widgetInfo.loadPreviewImage(this.context, 0)
        var previewWidth = drawable.intrinsicWidth
        var previewHeight = drawable.intrinsicHeight

        var scale = 1f
        val minPreviewWidth = 300
        val maxPreviewWidth = 400
        if (previewWidth < minPreviewWidth) {
            scale = minPreviewWidth.toFloat() / previewWidth
        }
        if (previewWidth > maxPreviewWidth) {
            scale = maxPreviewWidth.toFloat() / previewWidth
        }
        previewWidth = (scale * previewWidth).toInt()
        previewHeight = (scale * previewHeight).toInt()

        val c = Canvas()
        val preview = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_4444)
        c.setBitmap(preview)

        val x = (preview.width - previewWidth) / 2
        drawable.setBounds(x, 0, x + previewWidth, previewHeight)
        drawable.draw(c)

        image.setImageBitmap(preview)
    }
}