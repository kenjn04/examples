package com.example.hmi.myapplication2

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class WidgetContainerFragment : Fragment() {

    private lateinit var widgetContainer: WidgetContainer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_widget_container, container, false)
        widgetContainer = view.findViewById(R.id.widgetContainer)
        return view
    }

    fun addWidget(widget: WidgetFrame, x: Int, y: Int) {
        widgetContainer.addWidget(widget, x, y)
    }
}
