package vn.edu.greenwich.cw_1_sample.models

import java.util.*

data class Backup(
	var date: Date,
	var deviceName: String,
	var residents: ArrayList<Resident>,
	var requests: ArrayList<Request>,
) : java.io.Serializable
