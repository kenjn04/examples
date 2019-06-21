package jp.co.sample.components

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import jp.co.sample.components.list.VerticalListActivity
import jp.co.sample.components.list.VerticalListActivity2
import jp.co.sample.componentslibrary.slider.Slider
import jp.co.sample.componentslibrary.toggle.OnOffIndicator
import jp.co.sample.componentslibrary.toggle.ToggleSwitchLevel2
import jp.co.sample.componentslibrary.toggle.ToggleSwitchLevel3
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.setOnClickListener {
            intent = Intent(this, VerticalListActivity::class.java)
            startActivity(intent)
        }

        list2.setOnClickListener {
            intent = Intent(this, VerticalListActivity2::class.java)
            startActivity(intent)
        }

        indicator.setOnCheckedChangeListener(
            object: OnOffIndicator.OnCheckedChangeListener {
                override fun onCheckedChanged(isChecked: Boolean) {
                    Toast.makeText(this@MainActivity, "toggle status changed: ${isChecked}", Toast.LENGTH_SHORT).show()
                }
            }
        )

        toggle2.setOnLevelChangeListener(
            object: ToggleSwitchLevel2.OnLevelChangeListener {
                override fun onLevelChanged(level: Int) {
                    Toast.makeText(this@MainActivity, "toggle2 level changed: ${level}", Toast.LENGTH_SHORT).show()
                }
            }
        )

        toggle3.setOnLevelChangeListener(
            object: ToggleSwitchLevel3.OnLevelChangeListener {
                override fun onLevelChanged(level: Int) {
                    Toast.makeText(this@MainActivity, "toggle3 level changed: ${level}", Toast.LENGTH_SHORT).show()
                }
            }
        )

        slider.setOnLevelChangeListener(
            object: Slider.OnLevelChangeListener{
                override fun onLevelChanged(level: Int) {
                    Toast.makeText(this@MainActivity, "slider level changed: ${level}", Toast.LENGTH_SHORT).show()
                }

            }
        )

        val spinnerItems = listOf<String>("aaaaaaaaaaaaaaaa", "bbb", "ccc", "ddd", "eee",
            "fff", "ggg", "hhh", "iii", "jjj", "kkk", "lll", "mmm", "nnn", "ooo")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinnerItems
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner = findViewById<Spinner>(R.id.spinner)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }
        }
        spinner.dropDownVerticalOffset = 100

    }
}
