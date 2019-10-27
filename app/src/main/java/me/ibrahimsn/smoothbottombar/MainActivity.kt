package me.ibrahimsn.smoothbottombar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomBar.onItemSelected = { status.text = "Item $it selected" }
        bottomBar.onItemReselected = { status.text = "Item $it re-selected" }
    }
}
