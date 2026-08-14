package me.ibrahimsn.smoothbottombar

import android.graphics.Rect
import android.os.Bundle
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import kotlin.math.roundToInt

class AccessibleExploreByTouchHelper(
    private val host : SmoothBottomBar,
    // Supplier rather than a snapshot list - SmoothBottomBar.items is reassigned
    // whenever the menu is (re)populated after construction (e.g. itemMenuRes
    // set programmatically), and a captured List reference would go stale,
    // crashing with IndexOutOfBoundsException on the next accessibility/touch
    // callback (see issue #102).
    private val bottomBarItems : () -> List<BottomBarItem>,
    private val onClickAction : (id : Int) -> Unit,
    private val hasBadge : (index: Int) -> Boolean
) : ExploreByTouchHelper(host) {

    override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
        // defining simple ids for each item of the bottom bar
        for (i in bottomBarItems().indices) {
            virtualViewIds.add(i)
        }
    }

    override fun getVirtualViewAt(x: Float, y: Float): Int {
        val items = bottomBarItems()
        for (index in items.indices) {
            if (items[index].rect.contains(x, y)) {
                return index
            }
        }
        return HOST_ID
    }

    /**
     *  setBoundsInParent is required by [ExploreByTouchHelper]
     */
    @Suppress("DEPRECATION")
    override fun onPopulateNodeForVirtualView(
        virtualViewId: Int,
        node: AccessibilityNodeInfoCompat
    ) {
        val items = bottomBarItems()
        node.className = BottomBarItem::class.simpleName
        node.contentDescription = if (hasBadge(virtualViewId)) {
            "${items[virtualViewId].contentDescription}, has notification"
        } else {
            items[virtualViewId].contentDescription
        }
        node.isClickable = true
        node.isFocusable = true
        node.isScreenReaderFocusable = true

        node.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)

        node.isSelected = host.itemActiveIndex == virtualViewId

        val bottomItemBoundRect = updateBoundsForBottomItem(virtualViewId)
        node.setBoundsInParent(bottomItemBoundRect)
    }

    override fun onPerformActionForVirtualView(
        virtualViewId: Int,
        action: Int,
        arguments: Bundle?
    ): Boolean {
        if (action == AccessibilityNodeInfoCompat.ACTION_CLICK) {
            onClickAction.invoke(virtualViewId)
            return true
        }
        return false
    }

    private fun updateBoundsForBottomItem(index: Int): Rect {
        val itemRect = bottomBarItems()[index].rect
        return Rect(
            itemRect.left.roundToInt(),
            itemRect.top.roundToInt(),
            itemRect.right.roundToInt(),
            itemRect.bottom.roundToInt()
        )
    }
}
