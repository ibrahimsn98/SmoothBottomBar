package me.ibrahimsn.smoothbottombar.compose

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

// Matches android.view.animation.DecelerateInterpolator() (factor = 1):
// f(t) = 1 - (1-t)^2 - the same curve the View version's tab-switch
// animation uses, so both flavors feel identical.
private val DecelerateEasing = Easing { t -> 1f - (1f - t) * (1f - t) }

private const val PILL_LAYOUT_ID = "pill"
private fun itemLayoutId(index: Int) = "item_$index"

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
    val layoutDirection = LocalLayoutDirection.current
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

    Layout(
        modifier = modifier
            .background(backgroundColor, shape)
            .selectableGroup(),
        content = {
            if (validSelectedIndex) {
                Box(
                    Modifier
                        .layoutId(PILL_LAYOUT_ID)
                        .width(pillWidth)
                        .height(pillHeight)
                        .background(indicatorColor, RoundedCornerShape(indicatorRadius)),
                )
            }

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
                    // clipToBounds(): the label doesn't shrink with the item
                    // (only its alpha does) - without clipping, a fading-out
                    // label can render wider than its item's current animated
                    // width and visually bleed into the neighboring item.
                    modifier = Modifier
                        .layoutId(itemLayoutId(index))
                        .width(itemWidth)
                        .clipToBounds(),
                )
            }
        },
    ) { measurables, constraints ->
        // Items and the pill are placed here with plain place() and manual
        // RTL mirroring, rather than Row + a custom Arrangement +
        // onGloballyPositioned/localPositionOf tracking (the previous
        // design). That approach measured correctly but its *queried*
        // positions (via LayoutCoordinates) didn't agree with where content
        // actually painted once RTL was active - confirmed with logging
        // (positionInRoot and localPositionOf both reported items in plain
        // left-to-right order while the bar visually rendered mirrored),
        // which pointed at Compose's automatic RTL placement not applying
        // the way assumed for a custom Arrangement's children, not at a bug
        // in the tracking math itself. Computing and placing positions
        // explicitly here removes that ambiguity entirely: this function is
        // the only source of truth for where anything goes, item and pill
        // placement share the exact same numbers by construction, and
        // mirroring is one explicit, verifiable line instead of an implicit
        // framework behavior.
        val itemPlaceables = items.indices.map { index ->
            measurables.first { it.layoutId == itemLayoutId(index) }.measure(Constraints())
        }
        val pillPlaceable = if (validSelectedIndex) {
            measurables.first { it.layoutId == PILL_LAYOUT_ID }.measure(Constraints())
        } else {
            null
        }

        val containerWidthPx = constraints.maxWidth
        val containerHeightPx = if (constraints.hasBoundedHeight) {
            constraints.maxHeight
        } else {
            pillHeight.roundToPx()
        }

        val itemWidthsPx = itemPlaceables.map { it.width }
        val sideMarginsPx = sideMargins.roundToPx()
        val itemSpacingPx = itemSpacing.roundToPx()
        val contentWidthPx = itemWidthsPx.sum()
        val gapCount = (items.size - 1).coerceAtLeast(1)
        val gapPx = ((containerWidthPx - sideMarginsPx * 2 - contentWidthPx).toFloat() / gapCount)
            .coerceAtLeast(itemSpacingPx.toFloat())

        // Logical (composition-order) left edges, floor-spaced exactly like
        // the View's effectiveItemSpacing: itemSpacing is a floor, not a
        // fixed gap, and leftover space stretches the row edge-to-edge.
        val logicalX = IntArray(items.size)
        var cursor = sideMarginsPx
        for (i in items.indices) {
            logicalX[i] = cursor
            cursor += itemWidthsPx[i] + gapPx.roundToInt()
        }

        val isRtl = layoutDirection == LayoutDirection.Rtl
        val finalX = IntArray(items.size) { i ->
            if (isRtl) containerWidthPx - logicalX[i] - itemWidthsPx[i] else logicalX[i]
        }

        layout(containerWidthPx, containerHeightPx) {
            for (i in items.indices) {
                val placeable = itemPlaceables[i]
                placeable.place(finalX[i], (containerHeightPx - placeable.height) / 2)
            }
            if (pillPlaceable != null) {
                // Fixed recentering: the icon+label content isn't actually
                // centered on the item's own box (icon shifts one way, label
                // extends the other, by itemIconMargin), so the indicator is
                // nudged toward the content's true center - toward the
                // item's end side, whichever physical direction that is.
                val recenterPx = (itemIconMargin.roundToPx() / 2f) * (if (isRtl) -1 else 1)
                pillPlaceable.place(
                    x = (finalX[selectedIndex] + recenterPx).roundToInt(),
                    y = (containerHeightPx - pillPlaceable.height) / 2,
                )
            }
        }
    }
}
