package vn.edu.greenwich.cw_1_sample.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ResimaDbHelper(context: Context?) :
	SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

	override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
		sqLiteDatabase.execSQL(ResidentEntry.SQL_CREATE_TABLE.trimIndent())
		sqLiteDatabase.execSQL(RequestEntry.SQL_CREATE_TABLE.trimIndent())
	}

	override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
		sqLiteDatabase.execSQL(RequestEntry.SQL_DELETE_TABLE)
		sqLiteDatabase.execSQL(ResidentEntry.SQL_DELETE_TABLE)
		onCreate(sqLiteDatabase)
	}

	companion object {
		const val DATABASE_NAME = "Resima"
		const val DATABASE_VERSION = 1
	}
}
