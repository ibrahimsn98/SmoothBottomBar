package me.ibrahimsn.smoothbottombar

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import me.ibrahimsn.lib.Badge
import me.ibrahimsn.lib.BadgeType

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomBar.onItemSelected = {
            status.text = "Item $it selected"
        }

        bottomBar.onItemReselected = {
            status.text = "Item $it re-selected"
        }

        // Delay 1 second before drawing the badges ( just to notice the animation )
        bottomBar.postDelayed({
            bottomBar.setBadge(0, Badge(
                badgeSize = 12F,
                badgeColor = ContextCompat.getColor(this , R.color.colorBadge)
            ))

            bottomBar.setBadge(1, Badge(
                badgeSize = 20F,
                badgeBoxCornerRadius = 8F,
                badgeColor = ContextCompat.getColor(this , R.color.colorBadge),
                badgeText = "99+",
                badgeTextColor = Color.BLACK,
                badgeType = BadgeType.BOX
            ))
        } , 1000);

        // Remove the badge from item at index 0 after 4 seconds
        bottomBar.postDelayed({
            bottomBar.removeBadge(0)
        } , 4000);
    }
}
