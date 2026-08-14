package me.ibrahimsn.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.ibrahimsn.smoothbottombar.compose.SmoothBarItem
import me.ibrahimsn.smoothbottombar.compose.SmoothBottomBar

class ComposeMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeDemoScreen()
        }
    }
}

// ponytail: plain String routes, not the type-safe @Serializable route
// objects navigation-compose 2.9 supports - these are 4 flat, unchanging
// routes, adding kotlinx-serialization just for that isn't worth it.
private data class Destination(val route: String, val label: String, val iconRes: Int)

private val DESTINATIONS = listOf(
    Destination("dashboard", "Dashboard", R.drawable.ic_dashboard_white_24dp),
    Destination("leaderboard", "Leaderboard", R.drawable.ic_multiline_chart_white_24dp),
    Destination("store", "Store", R.drawable.ic_store_white_24dp),
    Destination("profile", "Profile", R.drawable.ic_person_outline_white_24dp),
)

@Composable
private fun ComposeDemoScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()

    // Badge demo parity with FirstFragment: badges on Dashboard (0) and
    // Store (2) from first composition - no view.post{} workaround needed,
    // there's no binding-init race in Compose.
    var badgeIndices by remember { mutableStateOf(setOf(0, 2)) }

    val items = DESTINATIONS.mapIndexed { index, destination ->
        SmoothBarItem(
            icon = painterResource(destination.iconRes),
            label = destination.label,
            hasBadge = index in badgeIndices,
        )
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val selectedIndex = DESTINATIONS.indexOfFirst { destination ->
        backStackEntry?.destination?.hierarchy?.any { it.route == destination.route } == true
    }.coerceAtLeast(0)

    fun navigate(index: Int) {
        navController.navigate(DESTINATIONS[index].route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(12.dp),
        ) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    // ponytail: view-based demo's own reverse link (MainActivity's
                    // overflow menu) already exists - this mirrors it so both demos
                    // are reachable in-app, no Scaffold/TopAppBar needed for one link.
                    .clickable { context.startActivity(Intent(context, MainActivity::class.java)) }
                    .semantics { contentDescription = "View Demo" },
            ) {
                BasicText(text = "View Demo", style = TextStyle(color = Color.White))
            }
        }

        NavHost(
            navController = navController,
            startDestination = DESTINATIONS[0].route,
            modifier = Modifier.weight(1f),
        ) {
            composable("dashboard") {
                PlaceholderScreen(
                    text = stringResource(R.string.fragment_dashboard_placeholder),
                    onClick = {
                        // Mirrors FirstFragment: tap navigates to Store and
                        // clears its badge. selectedIndex above updates
                        // itself via the nav callback above.
                        badgeIndices = badgeIndices - 2
                        navigate(2)
                    },
                )
            }
            composable("leaderboard") {
                PlaceholderScreen(stringResource(R.string.fragment_leaderboard_placeholder))
            }
            composable("store") {
                PlaceholderScreen(stringResource(R.string.fragment_store_placeholder))
            }
            composable("profile") {
                PlaceholderScreen(stringResource(R.string.fragment_profile_placeholder))
            }
        }

        SmoothBottomBar(
            items = items,
            selectedIndex = selectedIndex,
            onItemSelected = ::navigate,
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .navigationBarsPadding(),
            backgroundColor = Color(0xFF432FBF),
            indicatorColor = Color(0x2DFFFFFF),
            itemTextColor = Color.White,
            itemBadgeColor = Color(0xFFC7BFFA),
        )
    }
}

@Composable
private fun PlaceholderScreen(text: String, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        contentAlignment = Alignment.Center,
    ) {
        BasicText(text = text, style = TextStyle(color = Color.Black))
    }
}
