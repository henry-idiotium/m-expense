@file:Suppress("SameParameterValue")

package vn.edu.greenwich.cw_1_sample.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import vn.edu.greenwich.cw_1_sample.models.Request
import vn.edu.greenwich.cw_1_sample.models.Resident

data class ResimaDAO(val context: Context?) {
	private val _resimaDbHelper: ResimaDbHelper by lazy { ResimaDbHelper(context) }
	private val _dbWrite: SQLiteDatabase by lazy { _resimaDbHelper.readableDatabase }
	private val _dbRead: SQLiteDatabase by lazy { _resimaDbHelper.writableDatabase }

	fun close() {
		_dbRead.close()
		_dbWrite.close()
	}

	fun reset() = _resimaDbHelper.onUpgrade(_dbWrite, 0, 0)

	fun insertResident(resident: Resident): Long {
		val values = getResidentValues(resident)

		return _dbWrite.insert(ResidentEntry.TABLE_NAME, null, values)
	}

	fun getResidentList(
		resident: Resident?,
		orderByColumn: String?,
		isDesc: Boolean,
	): ArrayList<Resident> {
		val orderBy = getOrderByString(orderByColumn, isDesc)
		var selection: String? = null
		var args: Array<String>? = null

		if (resident != null) {
			val conditionList = ArrayList<String>()
			selection = ""

			if (strIsNotEmpty(resident.name)) {
				selection += " AND ${ResidentEntry.COL_NAME} LIKE ?"
				conditionList.add("%${resident.name}%")
			}
			if (strIsNotEmpty(resident.startDate)) {
				selection += " AND ${ResidentEntry.COL_START_DATE} = ?"
				conditionList.add(resident.startDate!!)
			}
			if (resident.owner != -1) {
				selection += " AND ${ResidentEntry.COL_OWNER} = ?"
				conditionList.add(java.lang.String.valueOf(resident.owner))
			}
			if (selection.trim { it <= ' ' }.isNotEmpty()) {
				selection = selection.substring(5)
			}

			args = conditionList.toTypedArray()
		}

		return getResidentFromDB(
			selection = selection,
			selectionArgs = args,
			orderBy = orderBy
		)
	}

	fun getResidentById(id: Long): Resident {
		val selection = "${ResidentEntry.COL_ID} = ?"
		val args = arrayOf(id.toString())

		return getResidentFromDB(
			selection = selection,
			selectionArgs = args
		)[0]
	}

	fun updateResident(resident: Resident): Long {
		val values = getResidentValues(resident)
		val selection = "${ResidentEntry.COL_ID} = ?"
		val selectionArgs = arrayOf<String>(java.lang.String.valueOf(resident.id))

		return _dbWrite
			.update(ResidentEntry.TABLE_NAME, values, selection, selectionArgs)
			.toLong()
	}

	fun deleteResident(id: Long): Long {
		val selection = "${ResidentEntry.COL_ID} = ?"
		val selectionArgs = arrayOf(id.toString())

		return _dbWrite
			.delete(ResidentEntry.TABLE_NAME, selection, selectionArgs)
			.toLong()
	}

	private fun getOrderByString(orderByColumn: String?, isDesc: Boolean): String? {
		if (orderByColumn == null || orderByColumn.trim { it <= ' ' }.isEmpty()) return null

		return if (isDesc) "${orderByColumn.trim { it <= ' ' }} DESC"
		else orderByColumn.trim { it <= ' ' }
	}

	private fun getResidentValues(resident: Resident): ContentValues {
		val values = ContentValues()
		values.put(ResidentEntry.COL_NAME, resident.name)
		values.put(ResidentEntry.COL_START_DATE, resident.startDate)
		values.put(ResidentEntry.COL_OWNER, resident.owner)

		return values
	}

	private fun getResidentFromDB(
		columns: Array<String?>? = null,
		selection: String? = null,
		selectionArgs: Array<String>? = null,
		groupBy: String? = null,
		having: String? = null,
		orderBy: String? = null,
	): ArrayList<Resident> {
		val list = ArrayList<Resident>()
		val cursor =
			_dbRead.query(ResidentEntry.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy)

		while (cursor.moveToNext()) {
			val residentItem = Resident()
			with(residentItem) {
				id = cursor.getLong(cursor.getColumnIndexOrThrow(ResidentEntry.COL_ID))
				name = cursor.getString(cursor.getColumnIndexOrThrow(ResidentEntry.COL_NAME))
				owner = cursor.getInt(cursor.getColumnIndexOrThrow(ResidentEntry.COL_OWNER))
				startDate = cursor.getString(cursor.getColumnIndexOrThrow(ResidentEntry.COL_START_DATE))
			}

			list.add(residentItem)
		}

		cursor.close()

		return list
	}

	// Request
	fun insertRequest(request: Request): Long {
		val values = getRequestValues(request)
		return _dbWrite.insert(RequestEntry.TABLE_NAME, null, values)
	}

	fun getRequestList(
		request: Request?,
		orderByColumn: String?,
		isDesc: Boolean,
	): ArrayList<Request> {
		val orderBy = getOrderByString(orderByColumn, isDesc)
		var selection: String? = null
		var selectionArgs: Array<String>? = null

		if (request != null) {
			selection = ""
			val conditionList = ArrayList<String>()

			if (request.content?.trim()?.isNotEmpty() == true) {
				selection += " AND ${RequestEntry.COL_CONTENT} LIKE ?"
				conditionList.add("%${request.content}%")
			}
			if (request.date?.trim()?.isNotEmpty() == true) {
				selection += " AND ${RequestEntry.COL_DATE} = ?"
				conditionList.add(request.date!!)
			}
			if (request.time?.trim()?.isNotEmpty() == true) {
				selection += " AND ${RequestEntry.COL_TIME} = ?"
				conditionList.add(request.time!!)
			}
			if (request.type?.trim()?.isNotEmpty() == true) {
				selection += " AND ${RequestEntry.COL_TYPE} = ?"
				conditionList.add(request.type!!)
			}
			if (request.residentId != -1L) {
				selection += " AND ${RequestEntry.COL_RESIDENT_ID} = ?"
				conditionList.add(java.lang.String.valueOf(request.residentId))
			}
			if (selection.trim { it <= ' ' }.isNotEmpty()) selection = selection.substring(5)

			selectionArgs = conditionList.toTypedArray()
		}

		return getRequestFromDB(null, selection, selectionArgs, null, null, orderBy)
	}

	fun getRequestById(id: Long): Request {
		val selection = "${RequestEntry.COL_ID} = ?"
		val selectionArgs = arrayOf(id.toString())

		return getRequestFromDB(null, selection, selectionArgs, null, null, null)[0]
	}

	fun updateRequest(request: Request): Long {
		val values = getRequestValues(request)
		val selection = "${RequestEntry.COL_ID} = ?"
		val selectionArgs = arrayOf<String>(java.lang.String.valueOf(request.id))

		return _dbWrite.update(RequestEntry.TABLE_NAME, values, selection, selectionArgs).toLong()
	}

	fun deleteRequest(id: Long): Long {
		val selection = "${RequestEntry.COL_ID} = ?"
		val selectionArgs = arrayOf(id.toString())

		return _dbWrite.delete(RequestEntry.TABLE_NAME, selection, selectionArgs).toLong()
	}

	private fun getRequestValues(request: Request): ContentValues {
		val values = ContentValues()
		values.put(RequestEntry.COL_CONTENT, request.content)
		values.put(RequestEntry.COL_DATE, request.date)
		values.put(RequestEntry.COL_TIME, request.time)
		values.put(RequestEntry.COL_TYPE, request.type)
		values.put(RequestEntry.COL_RESIDENT_ID, request.residentId)

		return values
	}

	private fun getRequestFromDB(
		columns: Array<String?>? = null,
		selection: String? = null,
		selectionArgs: Array<String>? = null,
		groupBy: String? = null,
		having: String? = null,
		orderBy: String? = null,
	): ArrayList<Request> {
		val cursor = _dbRead.query(RequestEntry.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy)
		val list = ArrayList<Request>()

		while (cursor.moveToNext()) {
			val requestItem = Request()
			with(requestItem) {
				id = cursor.getLong(cursor.getColumnIndexOrThrow(RequestEntry.COL_ID))
				date = cursor.getString(cursor.getColumnIndexOrThrow(RequestEntry.COL_DATE))
				time = cursor.getString(cursor.getColumnIndexOrThrow(RequestEntry.COL_TIME))
				type = cursor.getString(cursor.getColumnIndexOrThrow(RequestEntry.COL_TYPE))
				content = cursor.getString(cursor.getColumnIndexOrThrow(RequestEntry.COL_CONTENT))
				residentId = cursor.getLong(cursor.getColumnIndexOrThrow(RequestEntry.COL_RESIDENT_ID))
			}

			list.add(requestItem)
		}
		cursor.close()

		return list
	}

	private fun strIsNotEmpty(str: String?): Boolean = str?.trim()?.isNotEmpty() == true
}
