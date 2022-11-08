package vn.edu.greenwich.cw_1_sample.ui.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.databinding.FragmentResidentSearchBinding
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.dialog.CalendarFragment
import vn.edu.greenwich.cw_1_sample.utils.setWidthPercent

class ResidentSearchFragment : DialogFragment(R.layout.fragment_resident_search), CalendarFragment.FragmentListener {

	private lateinit var _binding: FragmentResidentSearchBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentResidentSearchBinding.inflate(inflater, container, false)

		setWidthPercent()

		_binding.btnSearchCancel.setOnClickListener { dismiss() }
		_binding.btnSearchSubmit.setOnClickListener { search() }
		_binding.textSearchDate.setOnTouchListener { v, motionEvent ->
			v.performClick()
			showCalendar(motionEvent)
		}

		return _binding.root
	}

	private fun search() {
		val resident = Resident()
		with(resident) {
			val date = _binding.textSearchDate.getText()
			val name = _binding.textSearchName.getText()

			if (date.trim { it <= ' ' }.isNotEmpty()) startDate = date
			if (name.trim { it <= ' ' }.isNotEmpty()) this.name = name
		}

		(requireParentFragment() as FragmentListener).sendFromResidentSearchFragment(resident)
		dismiss()
	}

	private fun showCalendar(motionEvent: MotionEvent): Boolean {
		if (motionEvent.action != MotionEvent.ACTION_DOWN) return false

		CalendarFragment().show(childFragmentManager, null)

		return true
	}

	override fun onCalendarChange(date: String) = _binding.textSearchDate.setText(date)

	interface FragmentListener {
		fun sendFromResidentSearchFragment(resident: Resident?)
	}
}
