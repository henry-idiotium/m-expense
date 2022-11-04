package vn.edu.greenwich.cw_1_sample.database

object RequestEntry {
	const val TABLE_NAME = "request"
	const val COL_ID = "id"
	const val COL_CONTENT = "content"
	const val COL_DATE = "date"
	const val COL_TIME = "time"
	const val COL_TYPE = "type"
	const val COL_RESIDENT_ID = "resident_id"
	const val SQL_CREATE_TABLE = """
		CREATE TABLE IF NOT EXISTS $TABLE_NAME (
			$COL_ID INTEGER PRIMARY KEY,
			$COL_CONTENT TEXT NOT NULL,
			$COL_DATE TEXT NOT NULL,
			$COL_TIME TEXT NOT NULL,
			$COL_TYPE INTEGER NOT NULL,
			$COL_RESIDENT_ID INTEGER NOT NULL,
			FOREIGN KEY($COL_RESIDENT_ID) REFERENCES ${ResidentEntry.TABLE_NAME}(${ResidentEntry.COL_ID})
		)"""
	const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}
