package vn.edu.greenwich.cw_1_sample.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.DialogFragment
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.databinding.FragmentCalendarBinding

class CalendarFragment : DialogFragment(R.layout.fragment_calendar) {

	private lateinit var _binding: FragmentCalendarBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentCalendarBinding.inflate(inflater, container, false)

		_binding.calendar.setOnDateChangeListener { _: CalendarView?, year: Int, month: Int, day: Int ->
			handleDateChange(year, month, day)
		}

		return _binding.root
	}

	private fun handleDateChange(year: Int, month: Int, day: Int) {
		val theMonthAfter = month + 1
		val date = """
			${if (day < 10) "0$day" else day}/
			${if (theMonthAfter < 10) "0$theMonthAfter" else theMonthAfter}/
			$year
		""".trimIndent().trimMargin()
			.replace("\n", "")

		// listener
		(requireParentFragment() as FragmentListener).onCalendarChange(date)
		dismiss()
	}

	interface FragmentListener {
		fun onCalendarChange(date: String)
	}
}
