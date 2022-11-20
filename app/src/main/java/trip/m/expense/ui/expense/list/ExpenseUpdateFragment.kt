package trip.m.expense.ui.expense.list

import android.annotation.SuppressLint
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
import trip.m.expense.databinding.FragmentExpenseUpdateBinding
import trip.m.expense.databinding.PartialExpenseUpsertFormBinding
import trip.m.expense.hideKeyboard
import trip.m.expense.models.Expense
import trip.m.expense.models.ExpenseType
import trip.m.expense.setFullScreen
import trip.m.expense.toLocalDate
import trip.m.expense.toSimpleDateFormat
import java.text.SimpleDateFormat
import java.util.*

class ExpenseUpdateFragment(private var _expense: Expense) : DialogFragment(R.layout.fragment_expense_update) {

	private lateinit var _binding: FragmentExpenseUpdateBinding
	private lateinit var _formBinding: PartialExpenseUpsertFormBinding
	private lateinit var _datePicker: MaterialDatePicker<Long>
	private lateinit var _db: DbDAO.ExpenseDbSet
	private lateinit var _confirmDialog: androidx.appcompat.app.AlertDialog

	override fun getTheme() = R.style.MExpense_Dialog_Fullscreen
	override fun onResume() = super.onResume().also { setFullScreen() }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentExpenseUpdateBinding.inflate(inflater, container, false)
		_formBinding = _binding.partialExpenseUpsertForm
		_db = DbDAO(requireActivity()).ExpenseDbSet()

		setFormValues()
		setChildViews()
		setDialogToolbar()
		setDatePicker()
		setConfirmDialog()

		return _binding.root
	}

	private fun setFormValues() = _formBinding.run {
		_expense.apply {
			dropdownType.setText(type.value)
			textInputDateOfExpense.setText(dateOfExpense.toSimpleDateFormat())
			textInputAmount.setText(amount.toString())
			textInputComment.setText(comment)
		}
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
				this@ExpenseUpdateFragment.dismiss()
			}.create()
	}

	@SuppressLint("ClickableViewAccessibility")
	private fun setChildViews() {
		_formBinding.run {
			textInputDateOfExpense.setInputOnTouchListener(::showDatePicker)
			dropdownType.setOnTouchListener(::showTypeDropdown)
		}
	}

	private fun setDialogToolbar() = _binding.partialDialogToolbar.run {
		btnConfirm.setOnClickListener { _confirmDialog.show() }
		btnDismissDialog.setOnClickListener { dismiss() }
	}

	private fun setDatePicker() {
		_datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date")
			.setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

		_datePicker.addOnPositiveButtonClickListener {
			_formBinding.textInputDateOfExpense.setText(
				_datePicker.selection?.let {
					SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
				}
			)
		}
	}

	private fun showTypeDropdown(v: View, event: MotionEvent): Boolean {
		if (event.action == MotionEvent.ACTION_UP) _formBinding.dropdownType.showDropDown()
		v.performClick()
		return v.onTouchEvent(event)
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
		with(_expense) {
			type = ExpenseType.get(dropdownType.text.toString())
			dateOfExpense = textInputDateOfExpense.getText().toLocalDate()
			amount = textInputAmount.getText().toIntOrNull()!!
			comment = textInputComment.getText()
		}
		_db.update(_expense)
	}

	interface FragmentListener {
		fun onUpdateExpense()
	}
}
