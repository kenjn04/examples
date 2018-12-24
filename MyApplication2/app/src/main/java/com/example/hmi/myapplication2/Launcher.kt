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

    private lateinit var containerConnector: WidgetContainerConnector

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
        containerConnector = findViewById(R.id.widget_container_connector)

//        var widgetContainers = mutableListOf<WidgetContainerView>()

        val colors = listOf(
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.BLACK
        )

        var j: Int = 0
        var k: Int = 1
        for (i in 1..(params.widgetContainerNum - 1)) {
            val frame1 = WidgetFrame(this, 1, 1, k++)
            val frame2 = WidgetFrame(this, 1, 1, k++)
//            val frame3 = WidgetFrame(this, 1, 1, k++)
            val frame4 = WidgetFrame(this, 2, 2, k++)

            frame1.setBackgroundColor(colors[j++ % colors.size])
            frame2.setBackgroundColor(colors[j++ % colors.size])
//            frame3.setBackgroundColor(colors[j++ % colors.size])
            frame4.setBackgroundColor(colors[j++ % colors.size])

            containerConnector.addWidget(frame1, i - 1, 0, 0)
            containerConnector.addWidget(frame2, i - 1, 1, 0)
//            containerConnector.addWidget(frame3, i - 1, 0, 1)
            containerConnector.addWidget(frame4, i - 1, 2, 0)
        }
        transitMode(LauncherMode.REARRANGE)
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
