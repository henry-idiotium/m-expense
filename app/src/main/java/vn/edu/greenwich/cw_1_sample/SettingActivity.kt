package vn.edu.greenwich.cw_1_sample

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_setting.*
import vn.edu.greenwich.cw_1_sample.database.BackupEntry
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.models.Backup
import java.util.*

class SettingActivity : AppCompatActivity(R.layout.activity_setting) {
	private lateinit var _db: ResimaDAO

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		_db = ResimaDAO(this)

		setTitle(R.string.label_setting)

		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		settingBackup.setOnClickListener { backup() }
		settingResetDatabase.setOnClickListener { resetDatabase() }
	}

	private fun backup() {
		val residents = _db.getResidentList(null, null, false)
		val requests = _db.getRequestList(null, null, false)

		if (residents.isNotEmpty()) {
			val deviceName =
				"${Build.MANUFACTURER} " +
					"${Build.MODEL} " +
					"${Build.VERSION.RELEASE} " +
					Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name
			val backup = Backup(Date(), deviceName, residents, requests)

			FirebaseFirestore.getInstance()
				.collection(BackupEntry.TABLE_NAME).add(backup)
				.addOnSuccessListener { document ->
					Toast.makeText(this, R.string.notification_backup_success, Toast.LENGTH_SHORT).show()
					Log.d(resources.getString(R.string.label_backup_firestore), document.id)
				}
				.addOnFailureListener { e ->
					Toast.makeText(this, R.string.notification_backup_fail, Toast.LENGTH_SHORT).show()
					e.printStackTrace()
				}

			return
		}

		Toast.makeText(this, R.string.error_empty_list, Toast.LENGTH_SHORT).show()
	}

	private fun resetDatabase() {
		_db.reset()
		Toast.makeText(this, R.string.label_reset_database, Toast.LENGTH_SHORT).show()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			onBackPressedDispatcher.onBackPressed()
			return true
		}

		return super.onOptionsItemSelected(item)
	}
}
