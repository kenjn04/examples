package com.example.hmi.myapplication2

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
    }

    fun setViews() {
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
}
