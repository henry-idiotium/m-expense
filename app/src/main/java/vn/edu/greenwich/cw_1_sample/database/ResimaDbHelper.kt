package vn.edu.greenwich.cw_1_sample.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ResimaDbHelper(viewCtx: Context?) :
	SQLiteOpenHelper(viewCtx, DATABASE_NAME, null, DATABASE_VERSION) {

	override fun onCreate(db: SQLiteDatabase) {
		db.execSQL(ResidentEntry.SQL_CREATE_TABLE.trimIndent())
		db.execSQL(RequestEntry.SQL_CREATE_TABLE.trimIndent())
	}

	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
		clear(db)
	}

	fun clear(db: SQLiteDatabase? = null): Boolean {
		val dbWrite = db ?: writableDatabase

		return try {
			dbWrite.execSQL(RequestEntry.SQL_DELETE_TABLE)
			dbWrite.execSQL(ResidentEntry.SQL_DELETE_TABLE)
			onCreate(dbWrite)

			true
		} catch (_: Exception) {
			false
		}
	}

	companion object {
		const val DATABASE_NAME = "Resima"
		const val DATABASE_VERSION = 1
	}
}
