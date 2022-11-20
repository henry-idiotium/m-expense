package trip.m.expense.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import trip.m.expense.R
import trip.m.expense.databinding.FragmentTripSearchBinding
import trip.m.expense.hideKeyboard
import trip.m.expense.models.Trip
import trip.m.expense.setBackgroundTransparent
import trip.m.expense.setWidthPercent
import trip.m.expense.toLocalDate
import java.text.SimpleDateFormat
import java.util.*

class TripSearchFragment : DialogFragment(R.layout.fragment_trip_search) {

	private lateinit var _binding: FragmentTripSearchBinding
	private lateinit var _datePicker: MaterialDatePicker<Long>

	override fun onResume() = super.onResume().also {
		setBackgroundTransparent()
		setWidthPercent()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTripSearchBinding.inflate(inflater, container, false)

		setChildViews()
		setDatePicker()

		return _binding.root
	}

	private fun setDatePicker() {
		_datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build()

		_datePicker.addOnPositiveButtonClickListener {
			_binding.textInputDateOfTrip.setText(
				_datePicker.selection?.let {
					SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
				}
			)
		}
	}

	private fun setChildViews() {
		_binding.btnSearch.setOnClickListener { search() }
		_binding.btnSearchCancel.setOnClickListener { dismiss() }
		_binding.textInputDateOfTrip.setInputOnTouchListener(::showDatePicker)
	}

	private fun showDatePicker(v: View, event: MotionEvent): Boolean {
		val handleResult = v.onTouchEvent(event)
		v.performClick()

		if (event.action == MotionEvent.ACTION_UP && (!_datePicker.isAdded || _datePicker.isDetached)) {
			_datePicker.show(requireActivity().supportFragmentManager, "CREATE_TRIP_DATE_PICKER")
			hideKeyboard()
		}

		return handleResult
	}

	private fun search() {
		fun notEmpty(value: String) = value.trim().isNotEmpty()

		val fmParent = requireParentFragment() as FragmentListener
		val trip = Trip().apply {
			_binding.textInputDateOfTrip.getText().let { dateOfTrip = if (notEmpty(it)) it.toLocalDate() else null }
			_binding.textInputDestination.getText().takeIf { notEmpty(it) }?.let { destination = it }
			_binding.textInputName.getText().takeIf { notEmpty(it) }?.let { name = it }
		}

		fmParent.onTripSearch(trip)
		dismiss()
	}

	interface FragmentListener {
		fun onTripSearch(trip: Trip?)
	}
}
