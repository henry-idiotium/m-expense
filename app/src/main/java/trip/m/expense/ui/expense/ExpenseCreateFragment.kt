package trip.m.expense.ui.expense

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import trip.m.expense.R
import trip.m.expense.database.DbDAO
import trip.m.expense.databinding.FragmentExpenseCreateBinding
import trip.m.expense.databinding.PartialExpenseUpsertFormBinding
import trip.m.expense.hideKeyboard
import trip.m.expense.models.Expense
import trip.m.expense.models.ExpenseType
import trip.m.expense.setFullScreen
import trip.m.expense.toLocalDate
import java.text.SimpleDateFormat
import java.util.*

class ExpenseCreateFragment(private val tripId: Long) : DialogFragment(R.layout.fragment_expense_create) {

	private lateinit var _binding: FragmentExpenseCreateBinding
	private lateinit var _formBinding: PartialExpenseUpsertFormBinding
	private lateinit var _datePicker: MaterialDatePicker<Long>
	private lateinit var _db: DbDAO.ExpenseDbSet

	override fun getTheme() = R.style.MExpense_Dialog_Fullscreen
	override fun onResume() = super.onResume().also { setFullScreen() }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentExpenseCreateBinding.inflate(inflater, container, false)
		_formBinding = _binding.partialExpenseUpsertForm
		_db = DbDAO(requireActivity()).ExpenseDbSet()

		setDatePicker()
		setChildViews()
		setDialogToolbar()

		return _binding.root
	}

	private fun setDatePicker() {
		_datePicker = MaterialDatePicker.Builder.datePicker()
			.setTitleText("Select date")
			.setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

		_datePicker.addOnPositiveButtonClickListener {
			_formBinding.textInputDateOfExpense.setText(
				_datePicker.selection?.let {
					SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
				}
			)
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	private fun setChildViews() = _formBinding.run {
		textInputDateOfExpense.setInputOnTouchListener(::showDatePicker)
		dropdownType.setOnTouchListener(::showTypeDropdown)
	}

	private fun setDialogToolbar() = _binding.partialDialogToolbar.run {
		btnConfirm.setOnClickListener { createExpense() }
		btnDismissDialog.setOnClickListener { dismiss() }
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

	private fun createExpense() {
		val expense = Expense(
			tripId = tripId,
			type = ExpenseType.get(_formBinding.dropdownType.text.toString().lowercase()),
			dateOfExpense = _formBinding.textInputDateOfExpense.getText().toLocalDate(),
			amount = _formBinding.textInputAmount.getText().toIntOrNull() ?: -1,
			comment = _formBinding.textInputComment.getText()
		)
		val succeeded = _db.insert(expense) != -1L

		val listener = requireParentFragment() as FragmentListener
		listener.onCreateExpense(succeeded)
		dismiss()
	}

	interface FragmentListener {
		fun onCreateExpense(succeeded: Boolean)
	}
}
