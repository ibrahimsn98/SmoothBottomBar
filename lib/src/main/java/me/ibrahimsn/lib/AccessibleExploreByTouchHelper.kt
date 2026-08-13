package me.ibrahimsn.lib

import android.graphics.Rect
import android.os.Bundle
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import kotlin.math.roundToInt

class AccessibleExploreByTouchHelper(
    private val host : SmoothBottomBar,
    private val bottomBarItems : List<BottomBarItem>,
    private val onClickAction : (id : Int) -> Unit,
    private val hasBadge : (index: Int) -> Boolean
) : ExploreByTouchHelper(host) {

    override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
        // defining simple ids for each item of the bottom bar
        for (i in bottomBarItems.indices) {
            virtualViewIds.add(i)
        }
    }

    override fun getVirtualViewAt(x: Float, y: Float): Int {
        for (index in bottomBarItems.indices) {
            if (bottomBarItems[index].rect.contains(x, y)) {
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
        node.className = BottomBarItem::class.simpleName
        node.contentDescription = if (hasBadge(virtualViewId)) {
            "${bottomBarItems[virtualViewId].contentDescription}, has notification"
        } else {
            bottomBarItems[virtualViewId].contentDescription
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
        val itemRect = bottomBarItems[index].rect
        return Rect(
            itemRect.left.roundToInt(),
            itemRect.top.roundToInt(),
            itemRect.right.roundToInt(),
            itemRect.bottom.roundToInt()
        )
    }
}
