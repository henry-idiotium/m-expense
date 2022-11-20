package trip.m.expense.models

import trip.m.expense.getCurrentDate
import trip.m.expense.toSimpleDateFormat
import java.time.LocalDate

data class Trip(
	var id: Long = -1,
	var name: String = "",
	var destination: String = "",
	var dateOfTrip: LocalDate? = getCurrentDate(),
	var riskAssessment: Boolean = false,
	var description: String? = null,
) : java.io.Serializable {
	override fun toString(): String = "[$name][$destination][${dateOfTrip?.toSimpleDateFormat()}]"
}
