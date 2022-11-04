package vn.edu.greenwich.cw_1_sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val navHostFragment = fragment_container_view.getFragment<NavHostFragment>()

		_navController = navHostFragment.navController
		nav_view.setupWithNavController(_navController)
		_appBarConfig = AppBarConfiguration(
			setOf(R.id.residentListFragment, R.id.residentRegisterFragment, R.id.aboutUsFragment),
			layout_main
		) // TODO: move resident register to resident list as a button

		setupActionBarWithNavController(_navController, _appBarConfig)

/*
		setupWithNavController(nav_view, navController)

		_appBarToggleBtn = ActionBarDrawerToggle(this, layout_main, open, close)
		layout_main.addDrawerListener(_appBarToggleBtn)
		_appBarToggleBtn.syncState()
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
*/
	}

	override fun onSupportNavigateUp(): Boolean {
		return _navController.navigateUp(_appBarConfig) || super.onSupportNavigateUp()
	}

/*
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_in_action, menu)
		return super.onCreateOptionsMenu(menu)
	}
*/

/*	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (_appBarToggleBtn.onOptionsItemSelected(item)) return true

		return super.onOptionsItemSelected(item)
//		return when (item.itemId) {
//			android.R.id.home -> {
//				onBackPressedDispatcher.onBackPressed()
//				true
//			}
//			R.id.setting -> {
//				startActivity(Intent(this, SettingActivity::class.java))
//				true
//			}
//			else -> super.onOptionsItemSelected(item)
//		}
	}*/
}
