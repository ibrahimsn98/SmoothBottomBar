package me.ibrahimsn.smoothbottombar.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

// ponytail: matches the View version's effectiveItemSpacing formula exactly -
// minGapPx is a floor, not a fixed gap; leftover space is distributed evenly
// across gaps so the row still spans edge-to-edge. No shrink-to-fit: if
// content doesn't fit, gaps floor out and items overflow, same as the View.
// Positions are computed in plain composition order - Row's own placement
// (placeRelative) handles RTL mirroring, so this doesn't need to branch on
// layoutDirection itself.
internal class FloorSpacedArrangement(private val minGapPx: Float) : Arrangement.Horizontal {

    override fun Density.arrange(
        totalSize: Int,
        sizes: IntArray,
        layoutDirection: LayoutDirection,
        outPositions: IntArray,
    ) {
        if (sizes.isEmpty()) return

        val contentSize = sizes.sum()
        val gapCount = (sizes.size - 1).coerceAtLeast(1)
        val gap = ((totalSize - contentSize).toFloat() / gapCount).coerceAtLeast(minGapPx)

        var position = 0f
        for (index in sizes.indices) {
            outPositions[index] = position.toInt()
            position += sizes[index] + gap
        }
    }
}
