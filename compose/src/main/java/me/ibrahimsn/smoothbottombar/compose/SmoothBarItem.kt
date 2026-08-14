package me.ibrahimsn.smoothbottombar.compose

import androidx.compose.ui.graphics.painter.Painter

data class SmoothBarItem(
    val icon: Painter,
    val label: String,
    val contentDescription: String = label,
    val hasBadge: Boolean = false,
)
