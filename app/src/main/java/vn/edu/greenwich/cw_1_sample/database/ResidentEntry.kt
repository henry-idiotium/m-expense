package vn.edu.greenwich.cw_1_sample.database

object ResidentEntry {
	const val TABLE_NAME = "resident"
	const val COL_ID = "id"
	const val COL_NAME = "name"
	const val COL_START_DATE = "start_date"
	const val COL_OWNER = "owner"
	const val SQL_CREATE_TABLE = """
		CREATE TABLE IF NOT EXISTS $TABLE_NAME(
			$COL_ID INTEGER PRIMARY KEY,
			$COL_NAME TEXT NOT NULL,
			$COL_START_DATE TEXT NOT NULL,
			$COL_OWNER INTEGER NULL
		)"""
	const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}
