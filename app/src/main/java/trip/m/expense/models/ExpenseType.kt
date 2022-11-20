package trip.m.expense.models

enum class ExpenseType(val value: String) {
	MEALS("meals"),
	OTHERS("others"),
	TRAVEL("travel"),
	HOTEL("supplies"),
	MILEAGE("mileage"),
	REPAIRS("repairs"),
	SUPPLIES("supplies"),
	HEATH_CARE("heath care"),
	REFRESHMENTS("refreshments"),
	TRANSPORT_RENTAL("transport rental");

	companion object {
		private val expenses = values().associateBy { it.value }
		infix fun get(name: String) = expenses[name.lowercase()]!!
	}
}
