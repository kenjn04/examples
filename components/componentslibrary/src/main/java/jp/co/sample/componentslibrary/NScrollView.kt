package jp.co.sample.componentslibrary

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import jp.co.sample.componentslibrary.list.NRecyclerView

class NScrollView(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
): ScrollView(context, attrs, defStyle) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

//    val recyclerView: NRecyclerView by lazy { findViewById<NRecyclerView>(R.id.recycler_view) }
    val linear: LinearLayout by lazy { getChildAt(0) as LinearLayout }
    val button: Button by lazy { linear.getChildAt(0) as Button }
    val list: NRecyclerView by lazy { linear.getChildAt(1) as NRecyclerView }

    init {
        /*
            val inflater = LayoutInflater.from(context)
            inflater.inflate(R.layout.scroll_with_button, this, true)
        */
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
    }
  override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("aaabbbcccdddeee1", "${linear.top} ${button.top} ${list.top}")
        Log.d("aaabbbcccdddeee2", "${scrollY}")
        return true
    }
}