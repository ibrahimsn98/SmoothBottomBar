package me.ibrahimsn.smoothbottombar.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

// Fixed, non-configurable badge ring width - the View version's own source
// already made and justified this exact call (a punch-hole ring in the bar's
// own background; only worth a dedicated attribute if a ring that doesn't
// match the bar is ever actually needed).
private val BADGE_RING_WIDTH = 2.dp

@Composable
internal fun BarItemContent(
    item: SmoothBarItem,
    selected: Boolean,
    alpha: Float,
    tint: Color,
    titleWidthPx: Float,
    onClick: () -> Unit,
    itemIconSize: Dp,
    itemIconMargin: Dp,
    itemTextColor: Color,
    itemTextSize: TextUnit,
    itemFontFamily: FontFamily?,
    itemBadgeColor: Color,
    itemBadgeRadius: Dp,
    badgeRingColor: Color,
    iconBackgroundColor: Color,
    iconBackgroundPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    // Icon shifts toward the item's start side as it becomes active, making
    // room for the label - magnitude only, Modifier.offset's Dp overload
    // mirrors automatically for RTL so no manual direction branching here.
    val iconShift = with(density) { (titleWidthPx / 2f * alpha).toDp() }

    Box(
        modifier = modifier
            .selectable(selected = selected, onClick = onClick, role = Role.Tab)
            .semantics {
                contentDescription = item.contentDescription
                if (item.hasBadge) stateDescription = "has notification"
            },
        contentAlignment = Alignment.Center,
    ) {
        // Icon background circle, inactive items only - matches the View's
        // drawIconBackground(). Unconditional Modifier.background instead of
        // an "is it transparent" branch: transparent paints nothing anyway.
        if (!selected) {
            Box(
                Modifier
                    .offset(x = -iconShift)
                    .size(itemIconSize + iconBackgroundPadding * 2)
                    .background(iconBackgroundColor, CircleShape),
            )
        }

        Image(
            painter = item.icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(tint),
            modifier = Modifier
                .offset(x = -iconShift)
                .size(itemIconSize),
        )

        BasicText(
            text = item.label,
            style = TextStyle(
                color = itemTextColor.copy(alpha = alpha),
                fontSize = itemTextSize,
                fontFamily = itemFontFamily,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.offset(x = itemIconSize / 2 + itemIconMargin),
        )

        if (item.hasBadge) {
            Box(
                modifier = Modifier
                    .offset(
                        x = -iconShift - itemIconSize / 2 + itemBadgeRadius,
                        y = -itemIconSize / 2 + itemBadgeRadius,
                    )
                    .size((itemBadgeRadius + BADGE_RING_WIDTH) * 2)
                    .background(badgeRingColor, CircleShape)
                    .padding(BADGE_RING_WIDTH)
                    .background(itemBadgeColor, CircleShape),
            )
        }
    }
}
