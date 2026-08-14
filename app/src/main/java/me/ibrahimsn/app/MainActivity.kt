package me.ibrahimsn.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

import me.ibrahimsn.app.databinding.ActivityMainBinding
import me.ibrahimsn.smoothbottombar.*
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        // Edge-to-edge is enforced on newer targetSdk levels, so the toolbar's
        // background now extends behind the status bar by default - push its
        // content down by the status bar inset instead of leaving it under it.
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            view.updatePadding(top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
            insets
        }
        // Same edge-to-edge issue at the bottom. Unlike Toolbar, SmoothBottomBar
        // never reads its own padding (onDraw/calculateItemBounds work off raw
        // width/height only), so padding here would be silently ignored - push
        // the whole view up with a bottom margin instead, which it does respect
        // via its ConstraintLayout position.
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomBar) { view, insets ->
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            }
            insets
        }
        // FragmentContainerView doesn't attach its nested NavHostFragment
        // synchronously during setContentView() the way the legacy <fragment>
        // tag did, so findNavController(viewId) throws here - fetch the
        // NavHostFragment from the FragmentManager directly instead (the
        // approach Android's own docs call for with FragmentContainerView).
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        // Set programmatically rather than via app:menu in the layout - the
        // common real-world pattern (also issue #102's exact repro) where the
        // bar's menu is attached after the view is already constructed.
        binding.bottomBar.itemMenuRes = R.menu.menu_bottom
        setupSmoothBottomMenu()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.another_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.another_item_1 -> {
                showToast(getString(R.string.toast_another_item_1))
                return true
            }

            R.id.another_item_2 -> {
                showToast(getString(R.string.toast_another_item_2))
                return true
            }

            R.id.another_item_3 -> {
                showToast(getString(R.string.toast_another_item_3))
                return true
            }

            R.id.menu_compose_demo -> {
                startActivity(Intent(this, ComposeMainActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //set an active fragment programmatically
    fun setSelectedItem(pos:Int){
        binding.bottomBar.setSelectedItem(pos)
    }
    //set badge indicator
    fun setBadge(pos:Int){
        binding.bottomBar.setBadge(pos)
    }
    //remove badge indicator
    fun removeBadge(pos:Int){
        binding.bottomBar.removeBadge(pos)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun setupSmoothBottomMenu() {
        binding.bottomBar.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
