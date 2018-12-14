package jp.co.nissan.hmi.myapplication

import android.graphics.drawable.Drawable

data class AppInfo(
        val label: CharSequence,
        val packageName: CharSequence,
        val icon: Drawable
)
