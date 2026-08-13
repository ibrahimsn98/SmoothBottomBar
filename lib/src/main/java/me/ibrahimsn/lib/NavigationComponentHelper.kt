package me.ibrahimsn.lib

import android.os.Bundle
import android.view.Menu
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.NavigationUI
import java.lang.ref.WeakReference
import java.util.WeakHashMap

/**
 * Created by Mayokun Adeniyi on 24/04/2020.
 */
class NavigationComponentHelper {

    companion object {

        // ponytail: WeakHashMap so entries don't keep a SmoothBottomBar alive;
        // tracks the currently-registered listener per bar so a second
        // setupWithNavController() call removes the first instead of both
        // firing forever on every navigation event.
        private val activeListeners =
            WeakHashMap<SmoothBottomBar, Pair<NavController, NavController.OnDestinationChangedListener>>()

        fun setupWithNavController(
            menu: Menu,
            smoothBottomBar: SmoothBottomBar,
            navController: NavController
        ) {
            smoothBottomBar.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelect(pos: Int): Boolean {
                    return NavigationUI.onNavDestinationSelected(menu.getItem(pos), navController)
                }
            }

            activeListeners.remove(smoothBottomBar)?.let { (oldController, oldListener) ->
                oldController.removeOnDestinationChangedListener(oldListener)
            }

            val weakReference = WeakReference(smoothBottomBar)

            val listener = object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    val view = weakReference.get()

                    if (view == null) {
                        navController.removeOnDestinationChangedListener(this)
                        return
                    }

                    for (h in 0 until menu.size()) {
                        val menuItem = menu.getItem(h)
                        if (matchDestination(destination, menuItem.itemId)) {
                            menuItem.isChecked = true
                            view.itemActiveIndex = h
                        }
                    }
                }
            }

            navController.addOnDestinationChangedListener(listener)
            activeListeners[smoothBottomBar] = navController to listener
        }

        /**
         * Determines whether the given `destId` matches the NavDestination. This handles
         * both the default case (the destination's id matches the given id) and the nested case where
         * the given id is a parent/grandparent/etc of the destination.
         */
        fun matchDestination(
            destination: NavDestination,
            @IdRes destId: Int
        ): Boolean {
            var currentDestination: NavDestination = destination
            while (currentDestination.id != destId) {
                currentDestination = currentDestination.parent ?: break
            }
            return currentDestination.id == destId
        }
    }
}
