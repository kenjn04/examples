package jp.co.sample.componentslibrary.toggle

import android.content.Context
import android.graphics.drawable.LevelListDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import jp.co.sample.componentslibrary.R

class ToggleSwitchLevel2(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
): LinearLayout(context, attrs, defStyle) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private val image: ImageView by lazy { findViewById<ImageView>(R.id.image) }
    private val option: TextView by lazy { findViewById<TextView>(R.id.option) }

    private var currentLevel: Int = 0
    private val maxLevel: Int = 2

    private var listener: OnLevelChangeListener? = null

    private val options = listOf<String>(
        "Option1",
        "Option2",
        "Option3"
    )

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.toggle_switch_level2, this, true)
        setLevel(currentLevel)
        setOnClickListener { increaseLevel() }
    }

    private fun increaseLevel() {
        setLevel((currentLevel + 1) % maxLevel)
    }

    fun setLevel(level: Int) {
        val drawable = image.drawable as LevelListDrawable
        drawable.level = level
        currentLevel = level
        option.text = options[level]
        listener?.onLevelChanged(level)
    }

    fun setOnLevelChangeListener(setListener: OnLevelChangeListener) {
        listener = setListener
    }

    interface OnLevelChangeListener {
        fun onLevelChanged(level: Int)
    }
}