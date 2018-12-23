package com.example.hmi.myapplication2

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import com.example.hmi.myapplication2.common.LauncherMode
import com.example.hmi.myapplication2.common.LauncherParams

class Launcher : AppCompatActivity() {

    lateinit var workspace: Workspace

    lateinit var params: LauncherParams

    var mode = LauncherMode.DISPLAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        params = LauncherParams(this)
        setContentView(R.layout.launcher)

        setViews()
    }

    private fun setViews() {

        workspace = findViewById(R.id.workspace)

        var widgetContainers = mutableListOf<WidgetContainerView>()

        val colors = listOf(
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.BLACK
        )

        var j: Int = 0
        var k: Int = 1
        for (i in 1..params.widgetContainerNum) {
            val widgetContainer = WidgetContainerView(this)
            val layoutParam = FrameLayout.LayoutParams(
                params.displaySize.x - 20, params.displaySize.y
            ).apply {
                gravity = Gravity.TOP or Gravity.LEFT
            }
            widgetContainer.layoutParams = layoutParam
            widgetContainer.setBackgroundColor(Color.GRAY)

            val frame1 = WidgetFrame(this, 1, 1, k++)
            val frame2 = WidgetFrame(this, 1, 1, k++)
            val frame3 = WidgetFrame(this, 2, 2, k++)
            val frame4 = WidgetFrame(this, 2, 1, k++)

//            frame1.setBackgroundColor(colors[j++ % colors.size])
            frame2.setBackgroundColor(colors[j++ % colors.size])
//            frame3.setBackgroundColor(colors[j++ % colors.size])
//            frame4.setBackgroundColor(colors[j++ % colors.size])

//            widgetContainerView.addWidget(frame1, 0, 0)
            widgetContainer.addWidget(frame2, 1, 0)
//            widgetContainerView.addWidget(frame3, 2, 0)
//            widgetContainerView.addWidget(frame4, 0, 1)

            widgetContainers.add(widgetContainer)
        }
        workspace.setWidgetContainers(widgetContainers)

//        transitMode(LauncherMode.REARRANGE)
    }

    fun transitMode(nextMode: LauncherMode) {
        when (nextMode) {
            LauncherMode.REARRANGE -> {
                workspace.shrink()
                mode = LauncherMode.REARRANGE
            }
        }
    }

}
