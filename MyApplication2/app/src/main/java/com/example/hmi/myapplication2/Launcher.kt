package com.example.hmi.myapplication2

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout

class Launcher : AppCompatActivity() {

    lateinit var dragLayer: FrameLayout
    lateinit var widgetContainer: WidgetContainerView
    private lateinit var frame1: WidgetFrame
    private lateinit var frame2: WidgetFrame
    private lateinit var frame3: WidgetFrame
    private lateinit var frame4: WidgetFrame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher)

        setViews()
        testQueue()
    }

    private fun setViews() {
        dragLayer = findViewById(R.id.drag_layer)
        widgetContainer = findViewById(R.id.widget_container)

        frame1 = WidgetFrame(this, 1, 1)
        frame2 = WidgetFrame(this, 1, 1)
        frame3 = WidgetFrame(this, 2, 2)
        frame4 = WidgetFrame(this, 2, 1)

        frame1.setBackgroundColor(Color.RED)
        frame2.setBackgroundColor(Color.GREEN)
        frame3.setBackgroundColor(Color.BLUE)
        frame4.setBackgroundColor(Color.BLACK)

        widgetContainer.addWidget(frame1, 0, 0)
        widgetContainer.addWidget(frame2, 1, 0)
        widgetContainer.addWidget(frame3, 2, 0)
        widgetContainer.addWidget(frame4, 0, 1)
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
