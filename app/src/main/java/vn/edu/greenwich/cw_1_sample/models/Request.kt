package vn.edu.greenwich.cw_1_sample.models

data class Request(
	var id: Long = -1,
	var content: String? = null,
	var date: String? = null,
	var time: String? = null,
	var type: String? = null,
	var residentId: Long = -1,
) : java.io.Serializable {

	fun isEmpty(): Boolean {
		return -1L == id &&
			null == content &&
			null == date &&
			null == time &&
			null == type &&
			-1L == residentId
	}

	override fun toString(): String = "[$type][$date] $content"
}
