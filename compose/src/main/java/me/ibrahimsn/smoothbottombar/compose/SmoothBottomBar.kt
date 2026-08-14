package me.ibrahimsn.smoothbottombar.compose

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Matches android.view.animation.DecelerateInterpolator() (factor = 1):
// f(t) = 1 - (1-t)^2 - the same curve the View version's tab-switch
// animation uses, so both flavors feel identical.
private val DecelerateEasing = Easing { t -> 1f - (1f - t) * (1f - t) }

/**
 * A fully Compose-native equivalent of the View-based `SmoothBottomBar`
 * (`me.ibrahimsn.smoothbottombar.SmoothBottomBar`). A controlled component -
 * [selectedIndex] is owned by the caller; this composable only reports
 * intent via [onItemSelected]/[onItemReselected].
 */
@Composable
fun SmoothBottomBar(
    items: List<SmoothBarItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onItemReselected: (Int) -> Unit = {},
    backgroundColor: Color = Color.White,
    shape: Shape = RectangleShape,
    indicatorColor: Color = Color(0x2DFFFFFF),
    indicatorRadius: Dp = 12.dp,
    sideMargins: Dp = 10.dp,
    itemPadding: Dp = 10.dp,
    itemSpacing: Dp = 8.dp,
    itemTextColor: Color = Color.White,
    itemTextSize: TextUnit = 11.sp,
    itemFontFamily: FontFamily? = null,
    itemBadgeColor: Color = Color.Red,
    itemBadgeRadius: Dp = 4.dp,
    itemIconSize: Dp = 18.dp,
    itemIconMargin: Dp = 4.dp,
    itemIconTint: Color = Color(0xC8FFFFFF),
    itemIconTintActive: Color = Color.White,
    itemAnimDurationMillis: Int = 200,
    iconBackgroundColor: Color = Color.Transparent,
    iconBackgroundPadding: Dp = 6.dp,
) {
    if (items.isEmpty()) {
        Box(modifier.background(backgroundColor, shape))
        return
    }

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    // Per-item measured label width, cached like the View's calculateItemBounds()
    // title-width cache - recomputed only when items/text style actually change,
    // not every frame.
    val titleWidths = remember(items, itemTextSize, itemFontFamily) {
        items.map { item ->
            textMeasurer.measure(
                text = item.label,
                style = TextStyle(
                    fontSize = itemTextSize,
                    fontFamily = itemFontFamily,
                    fontWeight = FontWeight.Bold,
                ),
            ).size.width.toFloat()
        }
    }

    val inactiveWidth = itemIconSize + itemPadding * 2
    fun activeWidth(index: Int): Dp {
        val titleWidthDp = with(density) { titleWidths[index].toDp() }
        return itemIconSize + itemIconMargin + titleWidthDp + itemPadding * 2
    }

    val validSelectedIndex = selectedIndex in items.indices
    val transition = updateTransition(targetState = selectedIndex, label = "SmoothBottomBar")
    val dpSpec = tween<Dp>(itemAnimDurationMillis, easing = DecelerateEasing)
    val floatSpec = tween<Float>(itemAnimDurationMillis, easing = DecelerateEasing)
    val colorSpec = tween<Color>(itemAnimDurationMillis, easing = DecelerateEasing)

    // Pill width always equals the active item's own animated width - driven
    // by the same shared Transition as every per-item width below, so the two
    // can never visually drift apart.
    val pillWidth by transition.animateDp(
        transitionSpec = { dpSpec },
        label = "pillWidth",
    ) { state -> if (state in items.indices) activeWidth(state) else 0.dp }
    val pillHeight = itemIconSize + itemPadding * 2

    // Pill x-position isn't animated on its own - it tracks the active item's
    // real, already-animating rendered position (same technique Material's
    // TabRow indicator uses), so indicator/item drift is structurally
    // impossible rather than something to keep in sync by hand.
    val itemCoordinates = remember { mutableStateMapOf<Int, LayoutCoordinates>() }
    var containerCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val activeCoordinates = itemCoordinates[selectedIndex]
    val container = containerCoordinates
    val pillOffsetX = if (validSelectedIndex && activeCoordinates != null && container != null &&
        container.isAttached && activeCoordinates.isAttached
    ) {
        with(density) { container.localPositionOf(activeCoordinates, Offset.Zero).x.toDp() }
    } else {
        0.dp
    }

    Box(
        modifier = modifier
            .background(backgroundColor, shape)
            .onGloballyPositioned { containerCoordinates = it },
    ) {
        if (validSelectedIndex) {
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = pillOffsetX)
                    // Fixed recentering: the icon+label content isn't
                    // actually centered on the item's own box (icon shifts
                    // one way, label extends the other, by itemIconMargin),
                    // so the indicator is nudged toward the content's true
                    // center. Dp-offset mirrors automatically in RTL.
                    .offset(x = itemIconMargin / 2)
                    .width(pillWidth)
                    .height(pillHeight)
                    .background(indicatorColor, RoundedCornerShape(indicatorRadius)),
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = sideMargins)
                .selectableGroup(),
            horizontalArrangement = FloorSpacedArrangement(
                minGapPx = with(density) { itemSpacing.toPx() },
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                val itemWidth by transition.animateDp(
                    transitionSpec = { dpSpec },
                    label = "width_$index",
                ) { state -> if (state == index) activeWidth(index) else inactiveWidth }
                val itemAlpha by transition.animateFloat(
                    transitionSpec = { floatSpec },
                    label = "alpha_$index",
                ) { state -> if (state == index) 1f else 0f }
                val itemTint by transition.animateColor(
                    transitionSpec = { colorSpec },
                    label = "tint_$index",
                ) { state -> if (state == index) itemIconTintActive else itemIconTint }

                BarItemContent(
                    item = item,
                    selected = index == selectedIndex,
                    alpha = itemAlpha,
                    tint = itemTint,
                    titleWidthPx = titleWidths[index],
                    onClick = {
                        if (index == selectedIndex) onItemReselected(index) else onItemSelected(index)
                    },
                    itemIconSize = itemIconSize,
                    itemIconMargin = itemIconMargin,
                    itemTextColor = itemTextColor,
                    itemTextSize = itemTextSize,
                    itemFontFamily = itemFontFamily,
                    itemBadgeColor = itemBadgeColor,
                    itemBadgeRadius = itemBadgeRadius,
                    badgeRingColor = backgroundColor,
                    iconBackgroundColor = iconBackgroundColor,
                    iconBackgroundPadding = iconBackgroundPadding,
                    // No fillMaxHeight() here: this composable has no fixed
                    // height of its own (the caller decides, same as the View
                    // needing an explicit layout_height) - items participating
                    // in the Column's non-weighted "natural size" pass would
                    // otherwise claim the full available screen height (that
                    // pass gives non-weighted children a bounded, not
                    // infinite, max height, so fillMaxHeight() isn't a no-op
                    // there), starving any sibling like a weight(1f) NavHost.
                    modifier = Modifier
                        .width(itemWidth)
                        .onGloballyPositioned { itemCoordinates[index] = it },
                )
            }
        }
    }
}
