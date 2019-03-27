package jp.co.sample.hmi.animation

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.AssertionError
import java.lang.Math.abs

class AnimateLinearLayoutManager(
    context: Context,
    orientation: Int,
    reverseLayout: Boolean
): LinearLayoutManager(context, orientation, reverseLayout) {

}