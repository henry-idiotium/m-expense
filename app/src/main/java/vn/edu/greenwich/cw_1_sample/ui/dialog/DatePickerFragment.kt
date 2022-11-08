package vn.edu.greenwich.cw_1_sample.ui.dialog

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import vn.edu.greenwich.cw_1_sample.R
import java.util.*

class DatePickerFragment : DialogFragment(R.layout.fragment_date_picker), OnDateSetListener {
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		super.onCreateDialog(savedInstanceState)

		val calendar = Calendar.getInstance()
		val year = calendar[Calendar.YEAR]
		val month = calendar[Calendar.MONTH]
		val day = calendar[Calendar.DAY_OF_MONTH]

		return DatePickerDialog(requireContext(), this, year, month, day)
	}

	override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
		/** DEBUG
		 * old: ++month
		 * remarks: idk why the old code can modify lambda parameter (month)
		 */
		val theMonthAfter = month + 1
		val date = """
			${if (day < 10) "0$day" else day}/
			${if (theMonthAfter < 10) "0$theMonthAfter" else theMonthAfter}/
			$year
		""".trimIndent()
			.trimMargin()
			.replace("\n", "")

		// listener
		(requireParentFragment() as FragmentListener).onDateChange(date)
		dismiss()
	}

	interface FragmentListener {
		fun onDateChange(date: String)
	}
}
