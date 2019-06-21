package jp.co.sample.components.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.co.sample.components.R
import jp.co.sample.componentslibrary.list.NRecyclerView

class HorizontalListActivity : AppCompatActivity() {

    private val recyclerView: NRecyclerView by lazy { findViewById<NRecyclerView>(R.id.recycler_view) }
    private val button: Button by lazy { findViewById<Button>(R.id.button) }
    private val switchButton: Button by lazy { findViewById<Button>(R.id.horizontal) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horizontal)

        setRecyclerView()

        button.text = recyclerView.scrollMode.toString()
        button.setOnClickListener {
            recyclerView.switchScrollMode()
            button.text = recyclerView.scrollMode.toString()
        }

        switchButton.setOnClickListener {
            val intent = Intent(this, VerticalListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HorizontalListActivity, RecyclerView.HORIZONTAL, false)
            adapter = NHorizontalRecyclerAdapter(this@HorizontalListActivity, generateFruitList())
        }
    }

    private fun generateFruitList(): MutableList<Int> {
        return mutableListOf(
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
