package trip.m.expense.models

import trip.m.expense.getCurrentDate
import java.time.LocalDate

data class Expense(
	var id: Long = -1,
	var type: ExpenseType = ExpenseType.MEALS,
	var dateOfExpense: LocalDate = getCurrentDate(),
	var amount: Int = 0,
	var comment: String? = null,
	var tripId: Long = -1,
) : java.io.Serializable
