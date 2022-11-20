package trip.m.expense.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DbContext(viewCtx: Context?) : SQLiteOpenHelper(viewCtx, DATABASE_NAME, null, DATABASE_VERSION) {

	override fun onCreate(db: SQLiteDatabase) {
		db.execSQL(TripEntry.SQL_CREATE_TABLE.trimIndent().trimMargin().replace("\n", ""))
		db.execSQL(ExpenseEntry.SQL_CREATE_TABLE.trimIndent().trimMargin().replace("\n", ""))
	}

	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
		clear(db)
	}

	fun clear(db: SQLiteDatabase? = null): Boolean {
		val dbWrite = db ?: writableDatabase

		return try {
			dbWrite.execSQL(ExpenseEntry.SQL_DELETE_TABLE)
			dbWrite.execSQL(TripEntry.SQL_DELETE_TABLE)
			onCreate(dbWrite)
			true
		} catch (e: Exception) {
			Log.e(this.toString(), e.message.toString())
			false
		}
	}

	companion object {
		const val DATABASE_NAME = "trip_m_expense"
		const val DATABASE_VERSION = 1
	}
}
