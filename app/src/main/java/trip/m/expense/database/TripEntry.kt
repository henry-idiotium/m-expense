package trip.m.expense.database

object TripEntry {
	const val TABLE_NAME = "trip"

	const val COL_ID = "id"
	const val COL_NAME = "name"
	const val COL_DESTINATION = "destination"
	const val COL_DATE_OF_TRIP = "date_of_trip"
	const val COL_RISK_ASSESSMENT = "risk_assessment"
	const val COL_DESCRIPTION = "description"

	const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
	const val SQL_CREATE_TABLE = """
		CREATE TABLE IF NOT EXISTS $TABLE_NAME(
			$COL_ID INTEGER PRIMARY KEY,
			$COL_NAME TEXT NOT NULL,
			$COL_DATE_OF_TRIP TEXT NOT NULL,
			$COL_RISK_ASSESSMENT BIT NOT NULL,
			$COL_DESTINATION TEXT NOT NULL,
			$COL_DESCRIPTION TEXT NULL
		)"""
}
