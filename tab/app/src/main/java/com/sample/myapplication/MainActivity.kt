package com.sample.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewGroupCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViews()

        btn.setOnClickListener {
            val current = tab.scrollX / 200
            if ((count - current) > 3) {
                tab.scrollX += 200
            }
        }
    }

    private fun setViews() {
        val manager = getSupportFragmentManager()
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val adapter = ExampleFragmentPagerAdapter(manager)
        viewPager.adapter = adapter

        count = adapter.count
        (0..(count - 1)).forEach {
            val i = it
            Log.d("aaaaa", "${it} ${adapter.getPageTitle(it)}")
            val textView = TextView(this)
            textView.text = adapter.getPageTitle(it)
            textView.textSize = 30F
            textView.gravity = Gravity.CENTER
            val param = ViewGroup.LayoutParams(
                200,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tab.addView(textView, param)
            textView.setOnClickListener {
                viewPager.setCurrentItem(i, false)
            }
        }

    }
}
