package jp.co.sample.componentslibrary.slider

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import jp.co.sample.componentslibrary.R
import kotlinx.android.synthetic.main.slider.view.*

class Slider(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
): LinearLayout(context, attrs, defStyle), SeekBar.OnSeekBarChangeListener {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private val seekBar: SeekBar by lazy { findViewById<SeekBar>(R.id.seekbar) }
    private val leftButton: Button by lazy { findViewById<Button>(R.id.left_button) }
    private val rightButton: Button by lazy { findViewById<Button>(R.id.right_button) }

    private var leftText = "+"
        set(value) {
            rightButton.text = value
            field = value
        }
    var rightText: String = "-"
        set(value) {
            leftButton.text = value
            field = value
        }

    private var listener: OnLevelChangeListener? = null

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.slider, this, true)

        seekBar.apply {
            max = 100
            progress = 50
            setOnSeekBarChangeListener(this@Slider)
        }
        leftButton.apply {
            setOnClickListener {
                seekBar.progress = seekBar.progress - 1
            }
            setOnLongClickListener {
                seekBar.progress = 0
                true
            }
        }
        rightButton.apply {
            setOnClickListener {
                seekBar.progress = seekBar.progress + 1
            }
            setOnLongClickListener {
                seekBar.progress = seekBar.max
                true
            }
        }
        leftText = leftText
        rightText = rightText

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        listener?.onLevelChanged(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) { /* Nothing to do */ }
    override fun onStopTrackingTouch(seekBar: SeekBar?) { /* Nothing to do */ }

    fun setLevel(level: Int) {
        seekBar.progress = level
    }

    fun setOnLevelChangeListener(setListener: OnLevelChangeListener) {
        listener = setListener
    }

    interface OnLevelChangeListener {
        fun onLevelChanged(level: Int)
    }
}