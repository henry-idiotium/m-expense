package vn.edu.greenwich.cw_1_sample.ui.resident

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_resident_search.*
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.dialog.CalendarFragment
import vn.edu.greenwich.cw_1_sample.utils.setWidthPercent

class ResidentSearchFragment : DialogFragment(R.layout.fragment_resident_search), CalendarFragment.FragmentListener {
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setWidthPercent()

		fmResidentSearchButtonCancel.setOnClickListener { dismiss() }
		fmResidentSearchButtonSearch.setOnClickListener { search() }
		fmResidentSearchDate.setOnTouchListener(fun(_: View, motionEvent: MotionEvent): Boolean {
			return showCalendar(motionEvent)
		})
	}

	private fun search() {
		val resident = Resident()
		with(resident) {
			val date = fmResidentSearchDate.text.toString()
			val name = fmResidentSearchName.text.toString()

			if (date.trim { it <= ' ' }.isNotEmpty()) startDate = date
			if (name.trim { it <= ' ' }.isNotEmpty()) this.name = name
		}

		(parentFragment as FragmentListener?)?.sendFromResidentSearchFragment(resident)
		dismiss()
	}

	private fun showCalendar(motionEvent: MotionEvent): Boolean {
		if (motionEvent.action != MotionEvent.ACTION_DOWN) return false

		CalendarFragment().show(childFragmentManager, null)

		return true
	}

	override fun sendFromCalendarFragment(date: String?) = fmResidentSearchDate.setText(date)

	interface FragmentListener {
		fun sendFromResidentSearchFragment(resident: Resident?)
	}
}
