package me.ibrahimsn.smoothbottombar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import me.ibrahimsn.lib.SmoothBottomBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomBar.setBottomBarCallback(object: SmoothBottomBar.BottomBarCallback {
            override fun onItemSelect(pos: Int) {

            }

            override fun onItemReselect(pos: Int) {

            }
        })
    }
}
