package trip.m.expense.models

import java.util.*

data class Backup(
	var date: Date,
	var deviceName: String,
	var trips: ArrayList<Trip>,
	var expenses: ArrayList<Expense>,
) : java.io.Serializable
