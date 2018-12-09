package com.example.hmi.home

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class Launcher : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher)
    }

    fun isWidgetsViewVisible(): Boolean {
        return true
    }

    companion object {

        fun getLauncher(context: Context): Launcher? {
            if (context is Launcher) {
                return context as Launcher
            }
            return null
        }
    }
}
