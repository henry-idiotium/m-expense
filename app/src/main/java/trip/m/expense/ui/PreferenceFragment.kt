package trip.m.expense.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.firebase.firestore.FirebaseFirestore
import trip.m.expense.R
import trip.m.expense.database.BackupEntry
import trip.m.expense.database.DbDAO
import trip.m.expense.isAppDarkThemeOn
import trip.m.expense.models.Backup
import java.util.*

class PreferenceFragment : PreferenceFragmentCompat() {

	private lateinit var _context: Context
	private lateinit var _dbDao: DbDAO
	private lateinit var _tripDb: DbDAO.TripDbSet
	private lateinit var _expenseDb: DbDAO.ExpenseDbSet
	private lateinit var _sharedPrefs: SharedPreferences
	private lateinit var _sharedPrefsEditor: SharedPreferences.Editor
	private val _preferences by lazy {
		object {
			val backup: Preference = getPreference(R.string.pref_back_up_key)
			val resetDb: Preference = getPreference(R.string.pref_reset_db_key)
			val darkMode: SwitchPreference = getPreference(R.string.pref_theme_mode_key)
		}
	}

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.preferences, rootKey)

		_context = requireContext()
		_dbDao = DbDAO(_context)
		_tripDb = _dbDao.TripDbSet()
		_expenseDb = _dbDao.ExpenseDbSet()
		_sharedPrefs = requireActivity().getSharedPreferences(getString(R.string.app_config), Context.MODE_PRIVATE)
		_sharedPrefsEditor = _sharedPrefs.edit()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		init()

		_preferences.run {
			backup.setOnPreferenceClickListener { backup() }
			resetDb.setOnPreferenceClickListener { resetDatabase() }
			darkMode.setOnPreferenceClickListener { toggleAppTheme() }
		}
	}

	private fun init() {
		_preferences.darkMode.isChecked = _context.isAppDarkThemeOn()
	}

	private fun toggleAppTheme(): Boolean {
		val isDark = _context.isAppDarkThemeOn()
		val mode = if (!isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

		AppCompatDelegate.setDefaultNightMode(mode)
		_sharedPrefsEditor.putInt(getString(R.string.app_config_theme_mode), mode).apply()
		_preferences.darkMode.isChecked = !isDark

		return true
	}

	private fun backup(): Boolean {
		val trips = _tripDb.get()
		val expenses = _expenseDb.get()

		if (trips.isEmpty()) {
			Toast.makeText(_context, R.string.error_empty_list, Toast.LENGTH_SHORT).show()
			return false
		}

		var succeeded = true
		val model = Build.MODEL
		val release = Build.VERSION.RELEASE
		val manufacturer = Build.MANUFACTURER
		val version = Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name

		val deviceName = arrayOf(manufacturer, model, release, version).joinToString(" ")
		val backup = Backup(Date(), deviceName, trips, expenses)

		FirebaseFirestore.getInstance().collection(BackupEntry.TABLE_NAME).add(backup)
			.addOnSuccessListener { document ->
				Toast.makeText(_context, R.string.notification_backup_success, Toast.LENGTH_SHORT).show()
				Log.d(resources.getString(R.string.label_backup_firestore), document.id)
			}.addOnFailureListener { e ->
				succeeded = false
				Toast.makeText(_context, R.string.notification_backup_fail, Toast.LENGTH_SHORT).show()
				e.printStackTrace()
			}

		return succeeded
	}

	private fun resetDatabase() = _dbDao.reset().let { status ->
		val messageId = if (status) R.string.label_reset_database else R.string.error_message_unknown_exception
		Toast.makeText(_context, messageId, Toast.LENGTH_SHORT).show()

		status
	}

	private inline fun <reified T : Preference> getPreference(key: Int): T {
		val keyValue = getString(key)

		return findPreference<T>(keyValue).takeIf { it != null }
			?: throw NullPointerException("$keyValue is not found in preference root")
	}
}
