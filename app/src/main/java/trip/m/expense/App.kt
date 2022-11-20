package trip.m.expense

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

	override fun onCreate() {
		super.onCreate()

		val sharedPrefs = getSharedPreferences(getString(R.string.app_config), Context.MODE_PRIVATE)
		val themeMode = sharedPrefs.getInt(getString(R.string.app_config_theme_mode), AppCompatDelegate.MODE_NIGHT_YES)
		AppCompatDelegate.setDefaultNightMode(themeMode)
	}
}
