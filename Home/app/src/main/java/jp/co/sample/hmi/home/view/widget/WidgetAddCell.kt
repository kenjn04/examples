package jp.co.sample.hmi.home.view.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import jp.co.sample.hmi.home.R
import jp.co.sample.hmi.home.view.HomeActivity
import jp.co.sample.hmi.home.view.HomeMode

class WidgetAddCell(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int
): WidgetCell(context, attrs, defStyle) {

    private val home: HomeActivity = context as HomeActivity

    lateinit var addButton: Button

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    fun deleteFromContainer() {
        widgetContainerView.removeWidget(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        addButton = findViewById(R.id.add_button)
        addButton.setOnClickListener {
            home.transitMode(HomeMode.SELECTION)
        }
    }
}