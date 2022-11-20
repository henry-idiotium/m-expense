package trip.m.expense

import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import trip.m.expense.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	private lateinit var _binding: ActivityMainBinding
	private lateinit var _appBarConfig: AppBarConfiguration
	private lateinit var _drawerToggle: ActionBarDrawerToggle
	private lateinit var _navController: NavController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		_binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(_binding.root)

		setToolbar()
		setNavigation()
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		android.R.id.home -> {
			val graph = _navController.graph
			val currentNavId = _navController.currentDestination?.id
			val topLevelIds = arrayOf(
				graph.startDestinationId,
				graph.findNode(R.id.nav_fm_about_us)?.id,
				graph.findNode(R.id.nav_fm_preferences)?.id
			)
			val openDrawer = topLevelIds.contains(currentNavId)

			if (openDrawer) _binding.drawerLayout.openDrawer(GravityCompat.START)
			// _drawerToggle.onOptionsItemSelected(item)

			openDrawer
		}
		else -> super.onOptionsItemSelected(item)
	}

	override fun onSupportNavigateUp(): Boolean {
		val navController = findNavController(R.id.nav_host_fragment)
		return navController.navigateUp(_appBarConfig)
	}

	/** Clear focus & soft keyboard on click outside of text box */
	override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
		val dispatchEvent = super.dispatchTouchEvent(ev)

		if (ev.action != MotionEvent.ACTION_DOWN) return dispatchEvent

		currentFocus.takeIf { it is EditText }?.run {
			val outRect = Rect()

			getGlobalVisibleRect(outRect)

			if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
				clearFocus()
				context.hideKeyboard(this)
			}
		}

		return dispatchEvent
	}

	private fun setNavigation() {
		val navView = _binding.navView
		val drawerLayout = _binding.drawerLayout
		val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
		val topLevelDes = setOf(R.id.nav_fm_home, R.id.nav_fm_preferences, R.id.nav_fm_about_us)

		_navController = navHostFragment.findNavController()
		_appBarConfig = AppBarConfiguration(topLevelDes, drawerLayout)
		setupActionBarWithNavController(_navController, _appBarConfig)
		navView.setupWithNavController(_navController)
	}

	private fun setToolbar() {
		val toolbar = _binding.appBarMain.toolbar
		val drawerLayout = _binding.drawerLayout
		val openRes = R.string.nav_drawer_open
		val closeRes = R.string.nav_drawer_close

		_drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, openRes, closeRes)
		_drawerToggle.syncState()

		setSupportActionBar(toolbar)
	}
}
