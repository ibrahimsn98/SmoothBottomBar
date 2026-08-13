package me.ibrahimsn.lib

import android.graphics.RectF
import android.graphics.drawable.Drawable

data class BottomBarItem (
    var title: String,
    var contentDescription : String,
    val icon: Drawable,
    var rect: RectF = RectF(),
    var alpha: Int,
    // Perf caches, recomputed in SmoothBottomBar.calculateItemBounds() /
    // tintAndDrawIcon() instead of every onDraw() frame.
    var titleWidth: Float = 0f,
    var lastAppliedTint: Int? = null
)
