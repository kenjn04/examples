package com.example.hmi.audio.view.adapter

import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.ListView
import com.example.hmi.audio.common.Element
import com.example.hmi.audio.common.Track

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("items")
    fun setItems(listView: ListView, list: MutableList<Element>?) {
        val adapter = listView.adapter as ElementListAdapter
        adapter.elementList = list
    }

    @JvmStatic
    @BindingAdapter("imageBitmap")
    fun setImage(imageView: ImageView, bitmap: Bitmap?) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageDrawable(null)
        }
    }
}