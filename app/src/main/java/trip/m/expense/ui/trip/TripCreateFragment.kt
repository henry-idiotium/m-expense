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
import trip.m.expense.databinding.FragmentTripCreateBinding
import trip.m.expense.databinding.PartialTripDetailBinding
import trip.m.expense.databinding.PartialTripUpsertFormBinding
import trip.m.expense.getCurrentDate
import trip.m.expense.hideKeyboard
import trip.m.expense.models.Trip
import trip.m.expense.setFullScreen
import trip.m.expense.toLocalDate
import trip.m.expense.toSimpleDateFormat
import trip.m.expense.ui.view.TextInputView
import java.text.SimpleDateFormat
import java.util.*

class TripCreateFragment : DialogFragment(R.layout.fragment_trip_create) {

	private lateinit var _binding: FragmentTripCreateBinding
	private lateinit var _formBinding: PartialTripUpsertFormBinding
	private lateinit var _datePicker: MaterialDatePicker<Long>
	private lateinit var _db: DbDAO.TripDbSet
	private lateinit var _confirmDialog: androidx.appcompat.app.AlertDialog

	override fun getTheme() = R.style.MExpense_Dialog_Fullscreen
	override fun onResume() = super.onResume().also { setFullScreen() }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTripCreateBinding.inflate(inflater, container, false)
		_formBinding = _binding.partialTripUpsertForm
		_db = DbDAO(requireActivity()).TripDbSet()

		setChildViews()
		setConfirmDialog()
		setDatePicker()

		return _binding.root
	}

	private fun setConfirmDialog() {
		_confirmDialog = MaterialAlertDialogBuilder(requireContext())
			.setTitle("Confirmation").setView(R.layout.partial_trip_detail_dialog)
			.setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
			.setPositiveButton("Confirm") { _, _ ->
				val tripId = _db.insert(getDataFromInput())
				val isNotValid = tripId == -1L
				val messageId = if (isNotValid) R.string.notification_failed
				else R.string.notification_succeeded

				Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()

				if (!isNotValid) {
					(requireParentFragment() as FragmentListener).onTripCreated(tripId)
					this@TripCreateFragment.dismiss()
				}
			}.create()
	}

	private fun showConfirmDialog() {
		if (!isValidForm()) return
		if (!_confirmDialog.isShowing) _confirmDialog.show()

		val trip = getDataFromInput()
		val layout = _confirmDialog.findViewById<View>(R.id.partial_trip_detail)!!

		PartialTripDetailBinding.bind(layout).run {
			layoutTripDetailTextName.setValue(trip.name)
			layoutTripDetailTextDesription.setValue(trip.description)
			layoutTripDetailTextDestination.setValue(trip.destination)
			layoutTripDetailTextDateOfTrip.setValue(trip.dateOfTrip?.toSimpleDateFormat())
			layoutTripDetailTextRiskAssessment.setValue(trip.riskAssessment.toString())
		}
	}

	private fun setChildViews() {
		_binding.partialDialogToolbar.btnConfirm.setOnClickListener { showConfirmDialog() }
		_binding.partialDialogToolbar.btnDismissDialog.setOnClickListener { dismiss() }
		_formBinding.textInputDateOfTrip.setInputOnTouchListener(::showDatePicker)
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

	private fun getDataFromInput() = with(_formBinding) {
		val name = textInputName.getText()
		val destination = textInputDestination.getText()
		val dateOfTrip = textInputDateOfTrip.getText().takeIf { it.isNotBlank() }
			?.toLocalDate()
			?: getCurrentDate()

		val description = textInputDescription.getText()
		val riskAssessment = switchRiskAssessment.isChecked

		Trip(id = -1, name, destination, dateOfTrip, riskAssessment, description)
	}

	private fun isValidForm() = with(_formBinding) {
		var errorCount = 0

		fun validated(v: TextInputView, name: String) {
			if (v.getText().isNotBlank() || v.getText().isNotEmpty()) return
			v.setError("$name cannot be empty")
			errorCount++
		}

		validated(textInputName, "Name")
		validated(textInputDestination, "Destination")
		validated(textInputDateOfTrip, "Date of Trip")

		errorCount == 0
	}

	interface FragmentListener {
		fun onTripCreated(tripId: Long)
	}
}
