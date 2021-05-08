package me.ibrahimsn.lib

import android.graphics.Rect
import android.os.Bundle
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper

class AccessibleExploreByTouchHelper(
    private val host : SmoothBottomBar,
    private val bottomBarItems : List<BottomBarItem>,
    private val onClickAction : (id : Int) -> Unit
) : ExploreByTouchHelper(host) {

    override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
        // defining simple ids for each item of the bottom bar
        for (i in bottomBarItems.indices) {
            virtualViewIds.add(i)
        }
    }

    override fun getVirtualViewAt(x: Float, y: Float): Int {
        val itemWidth = host.width / bottomBarItems.size
        return (x / itemWidth).toInt()
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
        node.contentDescription = bottomBarItems[virtualViewId].contentDescription
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
        val itemBounds = Rect()
        val itemWidth = host.width / bottomBarItems.size
        val left = index * itemWidth
        itemBounds.left = left
        itemBounds.top = 0
        itemBounds.right = (left + itemWidth)
        itemBounds.bottom = host.height
        return itemBounds
    }
}
