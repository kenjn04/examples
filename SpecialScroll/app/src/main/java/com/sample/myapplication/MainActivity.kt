package com.sample.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    private val dispalyX: Float = 1920F
    private val panelX: Float = 430F
    private val panelY: Float = 620F

    private val panelIds = listOf(
        R.id.panel1 /*,
        R.id.panel2,
        R.id.panel3,
        R.id.panel4
*/
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setPanels()
    }

    private fun setPanels() {
        val containerView = findViewById<ContainerView>(R.id.container)

        val startX = (dispalyX - (panelX * 4)) / 5
        var panelTranslationX = startX
        for (i in 0..(panelIds.size - 1)) {
            findViewById<PanelView>(panelIds[i]).apply {
                layoutParams.apply {
                    width = panelX.toInt()
                    height = panelY.toInt()
                }
                translationX = panelTranslationX
                containerView.registerListener(this)
            }
            panelTranslationX += startX + panelX
            break
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}
