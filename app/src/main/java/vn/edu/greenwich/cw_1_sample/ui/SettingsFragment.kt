package vn.edu.greenwich.cw_1_sample.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.firestore.FirebaseFirestore
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.database.BackupEntry
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.models.Backup
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {
	private lateinit var _context: Context
	private lateinit var _db: ResimaDAO
	private lateinit var _optionBackUp: Preference
	private lateinit var _optionResetDb: Preference

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.preferences, rootKey)

		_context = requireContext()
		_db = ResimaDAO(_context)
		_optionBackUp = getPreference(R.string.pref_back_up_key)
		_optionResetDb = getPreference(R.string.pref_reset_db_key)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		_optionBackUp.setOnPreferenceClickListener { backup() }
		_optionResetDb.setOnPreferenceClickListener { resetDatabase() }
	}

	private fun backup(): Boolean {
		val residents = _db.getResidentList(null, null, false)
		val requests = _db.getRequestList(null, null, false)

		if (residents.isEmpty()) {
			Toast.makeText(_context, R.string.error_empty_list, Toast.LENGTH_SHORT).show()
			return false
		}

		var succeeded = true
		val manufacturer = Build.MANUFACTURER
		val model = Build.MODEL
		val release = Build.VERSION.RELEASE
		val version = Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name

		val deviceName = arrayOf(manufacturer, model, release, version).joinToString(" ")
		val backup = Backup(Date(), deviceName, residents, requests)

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

	private fun resetDatabase(): Boolean {
		_db.reset().let {
			val messageId = if (it) R.string.label_reset_database else R.string.error_message_unknown_exception

			Toast.makeText(_context, messageId, Toast.LENGTH_SHORT).show()

			return it
		}
	}

	private fun getPreference(key: Int): Preference {
		val keyValue = getString(key)

		return findPreference<Preference>(keyValue).takeIf { it != null }
			?: throw NullPointerException("$keyValue is not found in preference root")
	}
}
