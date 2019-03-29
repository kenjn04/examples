package jp.co.sample.hmi.animation

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    private val recyclerView: AnimateRecyclerView by lazy { findViewById<AnimateRecyclerView>(R.id.recycler_view) }
    private val button: Button by lazy { findViewById<Button>(R.id.button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = AnimateRecyclerAdapter(this@MainActivity, generateFruitList())
            addOnScrollListener(AnimateOnScrollListener())
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        button.text = recyclerView.scrollMode.toString()
        button.setOnClickListener {
            recyclerView.switchScrollMode()
            button.text = recyclerView.scrollMode.toString()
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
                R.drawable.pineapple_pic
        )
    }
}
