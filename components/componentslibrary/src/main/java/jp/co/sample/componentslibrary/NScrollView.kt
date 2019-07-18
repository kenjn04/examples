package jp.co.sample.componentslibrary

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import jp.co.sample.componentslibrary.list.NRecyclerView
import kotlin.math.min

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
    val list: NRecyclerView2 by lazy { linear.getChildAt(1) as NRecyclerView2 }

    init {
        /*
            val inflater = LayoutInflater.from(context)
            inflater.inflate(R.layout.scroll_with_button, this, true)
        */
    }

    override fun onFinishInflate() {
        list.outerScrollView = this
        super.onFinishInflate()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("aaabbbcccdddeee2", "${scrollY} ${translationY}")
        if (button.height == scrollY) {
            list.fixed = true
        } else {
            list.fixed = false
        }
        return super.onInterceptTouchEvent(ev)
    }

}

class NRecyclerView2(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): NRecyclerView(context, attrs, defStyle) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    lateinit var outerScrollView: NScrollView

    var fixed: Boolean = false

    private var positionY: Float = 0F
    private var scrollY: Float = 0F

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return if (fixed) {
            super.onTouchEvent(e)
        } else {
            when (e!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    positionY = e.y
                }
                MotionEvent.ACTION_MOVE -> {
                    scrollY  += positionY - e.y
                    outerScrollView.scrollY = min(scrollY, 50F).toInt()
                    Log.d("aaabbbcccdddeee3", "${(positionY - e.y)} ${outerScrollView.scrollY} ${scrollY}")
                    positionY = e.y
                }
                else -> {
                }
            }
            true
        }
//        outerScrollView?.scrollY = 100
//        Log.d("aaabbbcccdddeee2", "${outerScrollView?.scrollY} aaa")
    }
}

