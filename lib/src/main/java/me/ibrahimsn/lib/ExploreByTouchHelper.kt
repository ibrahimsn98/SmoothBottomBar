package me.ibrahimsn.lib

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper

class AccessibleExploreByTouchHelper(private val host : View, private val bottomBarItems : List<BottomBarItem>) : ExploreByTouchHelper(host) {

    override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
        //defining simple ids for each item of the bottombar
        for( i in bottomBarItems.indices){
            virtualViewIds.add(i)
        }
    }

    override fun getVirtualViewAt(x: Float, y: Float): Int {
        val itemWidth = host.width / bottomBarItems.size
        return (x / itemWidth).toInt()
    }


    @Suppress("DEPRECATION") // setBoundsInParent is required by [ExploreByTouchHelper]
    override fun onPopulateNodeForVirtualView(
        virtualViewId: Int,
        node: AccessibilityNodeInfoCompat
    ) {
        Log.d("Fanny", "onPopulateNodeForVirtualView $virtualViewId")
        node.className = BottomBarItem::class.simpleName
        node.contentDescription = bottomBarItems[virtualViewId].title
        val bottomItemBoundRect = updateBoundsForBottomItem(virtualViewId)
        node.isClickable = true
        node.isFocusable = true
        node.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)

        node.isScreenReaderFocusable = true
        node.setBoundsInParent(bottomItemBoundRect)
    }

    override fun onPerformActionForVirtualView(
        virtualViewId: Int,
        action: Int,
        arguments: Bundle?
    ): Boolean {
        Log.d("Fanny","action $action")
        if(action == AccessibilityEvent.TYPE_VIEW_CLICKED){
            Log.d("Fanny", "sendAccessibilityEvent")
            sendAccessibilityEvent(host, action)
        }
        return true
    }

    override fun onPopulateAccessibilityEvent(host: View?, event: AccessibilityEvent) {
        super.onPopulateAccessibilityEvent(host, event)
        Log.d("Fanny", "onPopulateAccessibilityEvent $event")
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            event.text.add("Activé")
        }
    }


    private fun updateBoundsForBottomItem(index : Int) : Rect{
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