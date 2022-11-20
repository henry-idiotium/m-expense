package trip.m.expense.ui.expense

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import trip.m.expense.R
import trip.m.expense.database.DbDAO
import trip.m.expense.databinding.PartialExpenseDetailBinding
import trip.m.expense.models.Expense
import trip.m.expense.toSimpleDateFormat
import trip.m.expense.ui.expense.list.ExpenseUpdateFragment

class ExpenseDetailFragment(private val expenseId: Long) : DialogFragment(), ExpenseUpdateFragment.FragmentListener {

	private lateinit var _db: DbDAO.ExpenseDbSet
	private val _expense: Expense by lazy { _db.getById(expenseId) }
	private lateinit var _deleteDialog: androidx.appcompat.app.AlertDialog
	private lateinit var _mainDialog: androidx.appcompat.app.AlertDialog

	override fun onAttach(context: Context) = super.onAttach(context).also { _db = DbDAO(context).ExpenseDbSet() }

	override fun onResume() {
		super.onResume()

		val layout = _mainDialog.findViewById<View>(R.id.partial_expense_detail)!!
		PartialExpenseDetailBinding.bind(layout).run {
			layoutTextType.setValue(_expense.type.toString())
			layoutTextComment.setValue(_expense.comment)
			layoutTextAmount.setValue(_expense.amount.toString())
			layoutTextDateOfExpense.setValue(_expense.dateOfExpense.toSimpleDateFormat())
		}
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		super.onCreateDialog(savedInstanceState)
		setMainDialog()
		setDeleteDialog()
		return _mainDialog
	}

	private fun setDeleteDialog() {
		_deleteDialog = MaterialAlertDialogBuilder(requireContext())
			.setTitle("Are you sure to delete this?")
			.setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
			.setPositiveButton("Confirm") { _, _ ->
				val fmParent = requireParentFragment() as FragmentListener
				fmParent.onExpenseDeleted()
				dismiss()
			}.create()
	}

	private fun showUpdateDialog() {
		ExpenseUpdateFragment(_expense).show(parentFragmentManager, null)
	}

	private fun setMainDialog() {
		_mainDialog = MaterialAlertDialogBuilder(requireContext()).setTitle("Expense Details")
			.setView(R.layout.partial_expense_detail_dialog)
			.setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
			.setPositiveButton("Edit") { _, _ -> showUpdateDialog() }
			.setNegativeButton("Delete") { _, _ -> _deleteDialog.show() }
			.create()
	}

	override fun onUpdateExpense() {
		val fmParent = requireParentFragment() as FragmentListener
		fmParent.onExpenseUpdated()
	}

	interface FragmentListener {
		fun onExpenseDeleted()
		fun onExpenseUpdated()
	}
}
