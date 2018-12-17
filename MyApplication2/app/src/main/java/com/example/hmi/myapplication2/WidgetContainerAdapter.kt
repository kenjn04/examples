package com.example.hmi.myapplication2

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class WidgetContainerAdapter(
    fragmentManager: FragmentManager,
    val widgetContainerFragments: MutableList<WidgetContainerFragment>
): FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return widgetContainerFragments[position]
    }

    override fun getCount(): Int = widgetContainerFragments.size

}