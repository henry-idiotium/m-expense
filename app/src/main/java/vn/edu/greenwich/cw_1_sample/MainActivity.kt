package vn.edu.greenwich.cw_1_sample

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
	private lateinit var _navController: NavController
	private lateinit var _appBarConfig: AppBarConfiguration
	private lateinit var _menuToggle: ActionBarDrawerToggle

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val navHostFragment = fragment_container_view.getFragment<NavHostFragment>()

		_menuToggle = ActionBarDrawerToggle(this, layout_main, R.string.nav_drawer_close, R.string.nav_drawer_open)
		layout_main.addDrawerListener(_menuToggle)
		_menuToggle.syncState()

		_navController = navHostFragment.navController
		nav_view.setupWithNavController(_navController)

		_appBarConfig = AppBarConfiguration(_navController.graph, layout_main)

		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		setupActionBarWithNavController(_navController, _appBarConfig)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		var open = super.onOptionsItemSelected(item)

		if (layout_main.isDrawerOpen(GravityCompat.START)) {
			layout_main.closeDrawer(GravityCompat.START)
			open = true
		}

		return open
	}

	override fun onSupportNavigateUp(): Boolean {
		return _navController.navigateUp(_appBarConfig) || super.onSupportNavigateUp()
	}
}
