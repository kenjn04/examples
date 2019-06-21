package jp.co.sample.componentslibrary.toggle

import android.content.Context
import android.graphics.drawable.LevelListDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import jp.co.sample.componentslibrary.R

class OnOffIndicator(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
): LinearLayout(context, attrs, defStyle) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private val image: ImageView by lazy { findViewById<ImageView>(R.id.image) }
    private val state: TextView by lazy { findViewById<TextView>(R.id.state) }

    private var checked: Boolean = true

    private var listener: OnCheckedChangeListener? = null

    private val stateList = listOf<String>(
        "On",
        "Off"
    )

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.on_off_indicator, this, true)
        setChecked(checked)
        setOnClickListener { setChecked() }
    }

    private fun setChecked() {
        when (checked) {
            true -> {setChecked(false)}
            false -> {setChecked(true)}
        }
    }

    fun setChecked(isChecked: Boolean) {
        val drawable = image.drawable as LevelListDrawable
        when (isChecked) {
            true -> {
                drawable.level = 0
                state.text = stateList[0]
            }
            false -> {
                drawable.level = 1
                state.text = stateList[1]
            }
        }
        checked = isChecked
        listener?.onCheckedChanged(isChecked)
    }

    fun setOnCheckedChangeListener(setListener: OnCheckedChangeListener) {
        listener = setListener
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(isChecked: Boolean)
    }
}