package com.example.hmi.myapplication2

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import com.example.hmi.myapplication2.common.LauncherMode
import com.example.hmi.myapplication2.common.LauncherParams
import com.example.hmi.myapplication2.util.Queue

class Launcher : AppCompatActivity() {

    lateinit var launcherFrame: LauncherFrame
    lateinit var workspace: Workspace
    var widgetContainers = mutableListOf<WidgetContainerView>()

    var mode = LauncherMode.DISPLAY

    lateinit var params: LauncherParams

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        params = LauncherParams(this)
        setContentView(R.layout.launcher)

        setViews()
//        testQueue()
    }

    private fun setViews() {

//        widgetContainerView = findViewById(R.id.widet_container)
//        pagerFrame = findViewById(R.id.pager_frame)

//        pagerLayer = findViewById(R.id.pager_view)

        launcherFrame = findViewById(R.id.launcherFrame)
        workspace = findViewById(R.id.workspace)

        val colors = listOf(
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.BLACK
        )

        var j: Int = 0
        for (i in 1..params.widgetContainerNum) {
            val widgetContainer = WidgetContainerView(this)
            val layoutParam = FrameLayout.LayoutParams(
                params.widgetFrameWidth * params.widgetNumInContainerX,
                params.widgetFrameHeight * params.widgetNumInContainerY
            ).apply {
                gravity = Gravity.TOP or Gravity.LEFT
            }
            widgetContainer.layoutParams = layoutParam

            val frame1 = WidgetFrame(this, 1, 1)
            val frame2 = WidgetFrame(this, 1, 1)
            val frame3 = WidgetFrame(this, 2, 2)
            val frame4 = WidgetFrame(this, 2, 1)

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
        workspace.widgetContainerViews = widgetContainers
    }

    fun transitMode(nextMode: LauncherMode) {
        when (nextMode) {
            LauncherMode.REARRANGE -> {
                createTransitRearrangeModeAnimator().start()
                workspace.scale = 0.8F
                mode = LauncherMode.REARRANGE
            }
        }
    }

    private fun createTransitRearrangeModeAnimator(): ObjectAnimator {

        var holderX = PropertyValuesHolder.ofFloat("scaleX", 0.8F)
        var holderY = PropertyValuesHolder.ofFloat("scaleY", 0.8F)
        var objectAnimator = ObjectAnimator.ofPropertyValuesHolder(workspace, holderX, holderY)
        objectAnimator.duration = 100

        return objectAnimator
    }

    private fun testQueue() {
        val queue = Queue<Int>()
        queue.push(1)
        queue.push(2)
        queue.push(3)
        queue.push(4)
        queue.push(5)
        queue.push(6)
        Log.d("qqqqq", queue.size.toString())
        while (!queue.isEmpty()) {
            Log.d("qqqqq", queue.peek().toString())
            Log.d("qqqqq", queue.pop().toString())
        }
        queue.push(7)
        Log.d("qqqqqq", queue.contains(7).toString())
        Log.d("qqqqqq", queue.contains(6).toString())
        Log.d("qqqqqq", queue.containsSoFar(5).toString())
        Log.d("qqqqqq", queue.pushIfNotPushedBefore(5).toString())
        Log.d("qqqqqq", queue.pushIfNotPushedBefore(8).toString())
    }
}
