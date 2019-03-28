package jp.co.sample.hmi.animation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = AnimateRecyclerAdapter(this@MainActivity, generateFruitList())
            addOnScrollListener(AnimateOnScrollListener())
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }

    private fun generateFruitList(): List<Int> {
        return listOf(
                R.drawable.apple_pic,
                R.drawable.banana_pic,
                R.drawable.cherry_pic,
                R.drawable.grape_pic,
                R.drawable.mango_pic,
                R.drawable.orange_pic,
                R.drawable.pear_pic,
                R.drawable.pineapple_pic,
                R.drawable.strawberry_pic,
                R.drawable.watermelon_pic,
                R.drawable.apple_pic,
                R.drawable.banana_pic,
                R.drawable.cherry_pic,
                R.drawable.grape_pic,
                R.drawable.mango_pic,
                R.drawable.orange_pic,
                R.drawable.pear_pic,
                R.drawable.pineapple_pic,
                R.drawable.strawberry_pic,
                R.drawable.watermelon_pic,
                R.drawable.apple_pic,
                R.drawable.banana_pic,
                R.drawable.cherry_pic,
                R.drawable.grape_pic,
                R.drawable.mango_pic,
                R.drawable.orange_pic,
                R.drawable.pear_pic,
                R.drawable.pineapple_pic,
                R.drawable.strawberry_pic,
                R.drawable.watermelon_pic
        )
    }
}
