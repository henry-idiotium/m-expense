package trip.m.expense.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import trip.m.expense.R
import trip.m.expense.database.DbDAO
import trip.m.expense.databinding.FragmentTripUpdateBinding
import trip.m.expense.databinding.PartialTripUpsertFormBinding
import trip.m.expense.hideKeyboard
import trip.m.expense.models.Trip
import trip.m.expense.setFullScreen
import trip.m.expense.toLocalDate
import trip.m.expense.toSimpleDateFormat
import java.text.SimpleDateFormat
import java.util.*

class TripUpdateFragment(private val _trip: Trip) : DialogFragment(R.layout.fragment_trip_update) {

	private lateinit var _binding: FragmentTripUpdateBinding
	private lateinit var _formBinding: PartialTripUpsertFormBinding
	private lateinit var _datePicker: MaterialDatePicker<Long>
	private lateinit var _db: DbDAO.TripDbSet
	private lateinit var _confirmDialog: androidx.appcompat.app.AlertDialog

	override fun getTheme() = R.style.MExpense_Dialog_Fullscreen
	override fun onResume() = super.onResume().also { setFullScreen() }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTripUpdateBinding.inflate(inflater, container, false)
		_formBinding = _binding.partialTripUpsertForm
		_db = DbDAO(requireActivity()).TripDbSet()

		setFormValues()
		setChildViews()
		setDialogToolbar()
		setDatePicker()
		setConfirmDialog()

		return _binding.root
	}

	private fun setConfirmDialog() {
		_confirmDialog = MaterialAlertDialogBuilder(requireContext())
			.setTitle("Confirmation")
			.setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
			.setPositiveButton("Confirm") { _, _ ->
				val succeeded = update() == -1L
				val messageId = if (succeeded) R.string.notification_failed
				else R.string.notification_succeeded

				Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()

				if (!succeeded) {
					(requireParentFragment() as FragmentListener).onUpdateTrip()
					this@TripUpdateFragment.dismiss()
				}
			}.create()
	}

	private fun setFormValues() = _formBinding.run {
		_trip.apply {
			textInputName.setText(name)
			textInputDestination.setText(destination)
			textInputDateOfTrip.setText(dateOfTrip?.toSimpleDateFormat())
			textInputDescription.setText(description)
			switchRiskAssessment.isChecked = riskAssessment
		}
	}

	private fun setChildViews() {
		_formBinding.textInputDateOfTrip.setInputOnTouchListener(::showDatePicker)
	}

	private fun setDialogToolbar() = _binding.partialDialogToolbar.run {
		btnConfirm.setOnClickListener { _confirmDialog.show() }
		btnDismissDialog.setOnClickListener { dismiss() }
	}

	private fun setDatePicker() {
		_datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date")
			.setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

		_datePicker.addOnPositiveButtonClickListener {
			_formBinding.textInputDateOfTrip.setText(
				_datePicker.selection?.let {
					SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
				}
			)
		}
	}

	private fun showDatePicker(v: View, event: MotionEvent): Boolean {
		val handleResult = v.onTouchEvent(event)
		v.performClick()

		if (event.action == MotionEvent.ACTION_UP && (!_datePicker.isAdded || _datePicker.isDetached)) {
			_datePicker.show(requireActivity().supportFragmentManager, null)
			hideKeyboard()
		}

		return handleResult
	}

	private fun update() = _formBinding.run {
		with(_trip) {
			name = textInputName.getText()
			description = textInputDescription.getText()
			destination = textInputDestination.getText()
			riskAssessment = switchRiskAssessment.isChecked
			dateOfTrip = textInputDateOfTrip.getText().toLocalDate()
		}
		_db.update(_trip)
	}

	interface FragmentListener {
		fun onUpdateTrip()
	}
}
