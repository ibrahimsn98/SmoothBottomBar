package me.ibrahimsn.lib

import android.os.Bundle
import android.view.Menu
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.NavigationUI
import java.lang.ref.WeakReference

/**
 * Created by Mayokun Adeniyi on 24/04/2020.
 */
class NavigationComponentHelper {

    companion object {

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

            val weakReference = WeakReference(smoothBottomBar)

            navController.addOnDestinationChangedListener(object :
                NavController.OnDestinationChangedListener {

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
                            smoothBottomBar.itemActiveIndex = h
                        }
                    }
                }
            })
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
            var currentDestination: NavDestination? = destination
            while (currentDestination!!.id != destId && currentDestination.parent != null) {
                currentDestination = currentDestination.parent
            }
            return currentDestination.id == destId
        }
    }
}
