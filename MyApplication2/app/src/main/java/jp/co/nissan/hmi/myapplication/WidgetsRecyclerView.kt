package jp.co.nissan.hmi.myapplication

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class WidgetsRecyclerView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int

) : RecyclerView(context, attrs, defStyle) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

}