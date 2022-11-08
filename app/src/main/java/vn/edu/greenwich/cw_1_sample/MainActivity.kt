package vn.edu.greenwich.cw_1_sample

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import vn.edu.greenwich.cw_1_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	private lateinit var _binding: ActivityMainBinding
	private lateinit var _appBarConfig: AppBarConfiguration
	private lateinit var _drawerToggle: ActionBarDrawerToggle
	private lateinit var _navController: NavController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		_binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(_binding.root)

		val toolbar = _binding.appBarMain.toolbar
		val navView = _binding.navView
		val drawerLayout = _binding.drawerLayout
		val openRes = R.string.nav_drawer_open
		val closeRes = R.string.nav_drawer_close
		val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

		_navController = navHostFragment.findNavController()
		_drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, openRes, closeRes)
		_drawerToggle.syncState()

		setSupportActionBar(toolbar)

		_appBarConfig = AppBarConfiguration(setOf(R.id.fragment_resident_list), drawerLayout)

		setupActionBarWithNavController(_navController, _appBarConfig)
		navView.setupWithNavController(_navController)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			android.R.id.home -> {
				val currentNavId = _navController.currentDestination?.id
				// _navController.graph.findNode(R.id.fragment_settings)?.id
				val topLevelIds = arrayOf(_navController.graph.startDestinationId)
				val openDrawer = topLevelIds.contains(currentNavId)

				if (openDrawer) _drawerToggle.onOptionsItemSelected(item)

				openDrawer
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	override fun onSupportNavigateUp(): Boolean {
		val navController = findNavController(R.id.nav_host_fragment)

		return navController.navigateUp(_appBarConfig)
	}
}
