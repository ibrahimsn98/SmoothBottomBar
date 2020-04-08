package me.ibrahimsn.lib

import android.graphics.Color

/**
 * Created by BrookMG on 4/8/2020 in me.ibrahimsn.lib
inside the project SmoothBottomBar .
 */
data class Badge (
    var badgeSize: Float,
    var badgeText: String = "",
    var badgeColor: Int,
    var badgeTextColor: Int = Color.BLACK,
    var badgeBoxCornerRadius: Float = 8F,
    var badgeType: BadgeType = BadgeType.CIRCLE
)