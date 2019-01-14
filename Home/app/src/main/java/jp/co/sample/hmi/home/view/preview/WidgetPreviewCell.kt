package jp.co.sample.hmi.home.view.preview

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import jp.co.sample.hmi.home.R
import jp.co.sample.hmi.home.common.HomeAppWidgetProviderInfo

class WidgetPreviewCell(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
) : LinearLayout(context, attrs, defStyle) {

    lateinit var pInfo: HomeAppWidgetProviderInfo
    lateinit var image: ImageView
    lateinit var name: TextView
    lateinit var size: TextView

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onFinishInflate() {
        super.onFinishInflate()
        image = findViewById(R.id.widget_preview)
        name = findViewById(R.id.widget_name)
        size = findViewById(R.id.widget_size)
    }

    fun applyFromCellItem(info: HomeAppWidgetProviderInfo, packageManager: PackageManager) {
        pInfo = info
        name.text = pInfo.getLabel(packageManager)
        size.text = context.getString(R.string.widget_dims_format, pInfo.spanX, pInfo.spanY)
        // TODO; Is this required?
        size.contentDescription = context.getString(R.string.widget_accessible_dims_format, pInfo.spanX, pInfo.spanY)
    }

    fun ensurePreview() {

        val drawable = pInfo.loadPreviewImage(this.context, 0)
        var previewWidth = drawable.intrinsicWidth
        var previewHeight = drawable.intrinsicHeight

        var scale = 1f
        // TODO: Need to be updated
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