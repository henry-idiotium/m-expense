@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package trip.m.expense.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import trip.m.expense.models.Expense
import trip.m.expense.models.ExpenseType
import trip.m.expense.models.Trip
import java.time.LocalDate

open class DbDAO(contentContext: Context?) {
	protected val dbContext = DbContext(contentContext)
	protected val dbWrite: SQLiteDatabase = dbContext.writableDatabase
	protected val dbRead: SQLiteDatabase = dbContext.readableDatabase

	fun close() {
		dbRead.close()
		dbWrite.close()
	}

	fun reset() = dbContext.clear(dbWrite)

	inner class TripDbSet : CrudDbSet<Trip> {
		private val entry = TripEntry

		override fun get(entityArgs: Trip?, orderByColumn: String?, isDesc: Boolean): java.util.ArrayList<Trip> {
			val orderBy = getOrderByString(orderByColumn, isDesc)
			val sqlArgs = ConditionHelper()

			entityArgs?.run {
				with(sqlArgs) {
					addCondition("%$name%", name.trim().isNotEmpty(), "${entry.COL_NAME} LIKE ?")
					addCondition("%$destination%", destination.trim().isNotEmpty(), "${entry.COL_DESTINATION} LIKE ?")
					dateOfTrip?.toString()?.let {
						addCondition(it, it.trim().isNotEmpty(), "${entry.COL_DATE_OF_TRIP} = ?")
					}
				}
			}

			return getValuesFromDb(
				selection = sqlArgs.getSelections(),
				selectionArgs = sqlArgs.getConditions(),
				orderBy = orderBy
			)
		}

		override infix fun delete(id: Long): Int {
			val tripSelectionArgs = arrayOf(id.toString())
			// remove all expenses of this trip
			dbWrite.delete(ExpenseEntry.TABLE_NAME, "${ExpenseEntry.COL_TRIP_ID} = ?", tripSelectionArgs).toLong()
			return dbWrite.delete(entry.TABLE_NAME, "${entry.COL_ID} = ?", tripSelectionArgs)
		}

		override infix fun insert(entity: Trip): Long {
			val values = getContentValues(entity)
			return dbWrite.insert(entry.TABLE_NAME, null, values)
		}

		override infix fun update(entity: Trip): Long = dbWrite.update(
			entry.TABLE_NAME,
			getContentValues(entity),
			"${entry.COL_ID} = ?",
			arrayOf<String>(java.lang.String.valueOf(entity.id))
		).toLong()

		override infix fun getById(id: Long) =
			getValuesFromDb(selection = "${entry.COL_ID} = ?", selectionArgs = arrayOf(id.toString()))[0]

		private infix fun getContentValues(trip: Trip) = ContentValues().apply {
			put(entry.COL_NAME, trip.name)
			put(entry.COL_DATE_OF_TRIP, trip.dateOfTrip.toString())
			put(entry.COL_DESCRIPTION, trip.description)
			put(entry.COL_DESTINATION, trip.destination)
			put(entry.COL_RISK_ASSESSMENT, trip.riskAssessment)
		}

		private fun getValuesFromDb(
			columns: Array<String>? = null,
			selection: String? = null,
			selectionArgs: Array<String>? = null,
			groupBy: String? = null,
			having: String? = null,
			orderBy: String? = null,
		): java.util.ArrayList<Trip> {
			val list = java.util.ArrayList<Trip>()
			val cursor = dbRead.query(entry.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy)

			with(cursor) {
				while (moveToNext()) {
					val trip = Trip(
						id = getLong(getColumnIndexOrThrow(entry.COL_ID)),
						name = getString(getColumnIndexOrThrow(entry.COL_NAME)),
						dateOfTrip = LocalDate.parse(getString(getColumnIndexOrThrow(entry.COL_DATE_OF_TRIP))),
						destination = getString(getColumnIndexOrThrow(entry.COL_DESTINATION)),
						description = getString(getColumnIndexOrThrow(entry.COL_DESCRIPTION)),
						riskAssessment = getInt(getColumnIndexOrThrow(entry.COL_RISK_ASSESSMENT)) == 1
					)
					list.add(trip)
				}
			}

			cursor.close()
			return list
		}
	}

	inner class ExpenseDbSet : CrudDbSet<Expense> {
		private val entry = ExpenseEntry

		override fun get(entityArgs: Expense?, orderByColumn: String?, isDesc: Boolean): java.util.ArrayList<Expense> {
			val orderBy = getOrderByString(orderByColumn, isDesc)
			val sqlArgs = ConditionHelper()

			entityArgs?.run {
				with(sqlArgs) {
					entityArgs.tripId.let { addCondition(it.toString(), it != -1L, "${entry.COL_TRIP_ID} = ?") }
				}
			}

			return getValuesFromDb(
				selection = sqlArgs.getSelections(),
				selectionArgs = sqlArgs.getConditions(),
				orderBy = orderBy
			)
		}

		override infix fun delete(id: Long) =
			dbWrite.delete(entry.TABLE_NAME, "${entry.COL_ID} = ?", arrayOf(id.toString()))

		infix fun deleteByTripId(id: Long) =
			dbWrite.delete(entry.TABLE_NAME, "${entry.COL_TRIP_ID} = ?", arrayOf(id.toString()))

		override infix fun insert(entity: Expense) =
			dbWrite.insert(entry.TABLE_NAME, null, getContentValues(entity))

		override infix fun update(entity: Expense): Long = dbWrite.update(
			entry.TABLE_NAME,
			getContentValues(entity),
			"${entry.COL_ID} = ?",
			arrayOf<String>(java.lang.String.valueOf(entity.id))
		).toLong()

		override infix fun getById(id: Long) =
			getValuesFromDb(selection = "${entry.COL_ID} = ?", selectionArgs = arrayOf(id.toString()))[0]

		private infix fun getContentValues(trip: Expense) = ContentValues().apply {
			put(entry.COL_TYPE, trip.type.ordinal)
			put(entry.COL_DATE_OF_EXPENSE, trip.dateOfExpense.toString())
			put(entry.COL_COMMENT, trip.comment)
			put(entry.COL_TRIP_ID, trip.tripId)
			put(entry.COL_AMOUNT, trip.amount)
		}

		private fun getValuesFromDb(
			columns: Array<String>? = null,
			selection: String? = null,
			selectionArgs: Array<String>? = null,
			orderBy: String? = null,
		): java.util.ArrayList<Expense> {
			val list = java.util.ArrayList<Expense>()
			val cursor = dbRead.query(entry.TABLE_NAME, columns, selection, selectionArgs, null, null, orderBy)

			with(cursor) {
				while (moveToNext()) {
					val expense = Expense(
						id = cursor.getLong(cursor.getColumnIndexOrThrow(entry.COL_ID)),
						type = ExpenseType.values()[cursor.getInt(cursor.getColumnIndexOrThrow(entry.COL_TYPE))],
						comment = cursor.getString(cursor.getColumnIndexOrThrow(entry.COL_COMMENT)),
						amount = cursor.getInt(cursor.getColumnIndexOrThrow(entry.COL_AMOUNT)),
						tripId = cursor.getLong(cursor.getColumnIndexOrThrow(entry.COL_TRIP_ID)),
						dateOfExpense = LocalDate.parse(
							cursor.getString(cursor.getColumnIndexOrThrow(entry.COL_DATE_OF_EXPENSE))
						)
					)
					list.add(expense)
				}
			}

			cursor.close()
			return list
		}
	}

	private inner class ConditionHelper {
		private val selections = ArrayList<String>()
		private val conditions = ArrayList<String>()

		fun addCondition(condition: String, addable: Boolean?, sqlMatch: String) {
			addable.takeIf { it == true } ?: return
			conditions.add(condition)
			selections.add(sqlMatch)
		}

		fun getSelections() = if (selections.isNotEmpty()) selections.joinToString(" AND ") else null
		fun getConditions() = conditions.toTypedArray()
	}

	private fun getOrderByString(orderByColumn: String?, isDesc: Boolean): String? {
		if (orderByColumn == null || orderByColumn.trim { it <= ' ' }.isEmpty()) return null

		return if (isDesc) "${orderByColumn.trim { it <= ' ' }} DESC"
		else orderByColumn.trim { it <= ' ' }
	}

	interface CrudDbSet<TEntity> {
		fun get(entityArgs: TEntity? = null, orderByColumn: String? = null, isDesc: Boolean = false):
			java.util.ArrayList<TEntity>

		infix fun getById(id: Long): TEntity
		infix fun insert(entity: TEntity): Long
		infix fun update(entity: TEntity): Long
		infix fun delete(id: Long): Int
	}
}
