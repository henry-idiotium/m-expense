package vn.edu.greenwich.cw_1_sample.models

data class Resident(
	var id: Long = -1,
	var name: String? = null,
	var startDate: String? = null,
	var owner: Int = -1,
) : java.io.Serializable {

	fun isEmpty(): Boolean {
		return -1L == id && null == name && null == startDate && -1 == owner
	}

	override fun toString(): String = "[$startDate] $name"
}
