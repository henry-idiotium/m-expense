package trip.m.expense.ui.trip

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import trip.m.expense.R
import trip.m.expense.database.DbDAO
import trip.m.expense.databinding.FragmentTripDetailBinding
import trip.m.expense.models.Trip
import trip.m.expense.serializable
import trip.m.expense.toSimpleDateFormat
import trip.m.expense.ui.expense.ExpenseCreateFragment
import trip.m.expense.ui.expense.ExpenseDetailFragment
import trip.m.expense.ui.expense.list.ExpenseListFragment

class TripDetailFragment :
	Fragment(R.layout.fragment_trip_detail),
	ExpenseCreateFragment.FragmentListener,
	TripUpdateFragment.FragmentListener,
	ExpenseDetailFragment.FragmentListener,
	MenuProvider {

	private lateinit var _binding: FragmentTripDetailBinding
	private lateinit var _navController: NavController
	private lateinit var _tripDb: DbDAO.TripDbSet
	private lateinit var _expenseDb: DbDAO.ExpenseDbSet
	private lateinit var _trip: Trip
	private lateinit var _deleteDialog: androidx.appcompat.app.AlertDialog

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTripDetailBinding.inflate(inflater, container, false)
		_navController = requireParentFragment().findNavController()
		_expenseDb = DbDAO(context).ExpenseDbSet()
		_tripDb = DbDAO(context).TripDbSet()

		requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
		setChildViews()
		setDetails()
		setExpenseList()
		setDeleteDialog()

		return _binding.root
	}

	private fun setChildViews() = _binding.run {
		btnAddExpense.setOnClickListener { ExpenseCreateFragment(_trip.id).show(childFragmentManager, null) }
	}

	@SuppressLint("RestrictedApi")
	override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.menu_trip_detail, menu)

		if (menu is MenuBuilder) {
			menu.setOptionalIconsVisible(true)
		}
	}

	override fun onUpdateTrip() = setDetails()

	override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
		when (menuItem.itemId) {
			R.id.menu_action_trip_detail_update -> TripUpdateFragment(_trip).show(childFragmentManager, null)
			R.id.menu_action_trip_detail_delete -> _deleteDialog.show()
			android.R.id.home -> _navController.navigateUp()
		}
		return true
	}

	override fun onCreateExpense(succeeded: Boolean) {
		val messageId = if (succeeded) R.string.notification_succeeded else R.string.notification_failed
		Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
		reloadExpenseList()
	}

	private fun setDeleteDialog() {
		_deleteDialog = MaterialAlertDialogBuilder(requireContext())
			.setTitle("Are you sure to delete this?")
			.setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
			.setPositiveButton("Confirm") { _, _ ->
				_expenseDb.deleteByTripId(_trip.id)

				val succeeded = _tripDb.delete(_trip.id) > 0
				val messageId = if (succeeded) R.string.notification_delete_success
				else R.string.notification_delete_fail

				if (succeeded) findNavController().navigateUp()

				Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
			}.create()
	}

	private fun setDetails() {
		val tripId = if (::_trip.isInitialized) _trip.id else requireArguments().serializable<Long>(ARG_PARAM_TRIP_ID)!!
		_trip = _tripDb.getById(tripId)

		_binding.partialTripDetail.run {
			layoutTripDetailTextName.setValue(_trip.name)
			layoutTripDetailTextDesription.setValue(_trip.description)
			layoutTripDetailTextDestination.setValue(_trip.destination)
			layoutTripDetailTextDateOfTrip.setValue(_trip.dateOfTrip?.toSimpleDateFormat())
			layoutTripDetailTextRiskAssessment.setValue(_trip.riskAssessment.toString())
		}
	}

	private fun setExpenseList() {
		val bundle = Bundle().apply { putSerializable(ExpenseListFragment.ARG_PARAM_TRIP_ID, _trip.id) }
		childFragmentManager.fragments[0].arguments = bundle
	}

	private fun reloadExpenseList() {
		childFragmentManager.beginTransaction().setReorderingAllowed(true).replace(
			R.id.list_expenses,
			ExpenseListFragment::class.java,
			Bundle().apply { putSerializable(ExpenseListFragment.ARG_PARAM_TRIP_ID, _trip.id) }
		).commit()
	}

	override fun onExpenseDeleted() = reloadExpenseList()
	override fun onExpenseUpdated() = reloadExpenseList()

	companion object {
		const val ARG_PARAM_TRIP_ID = "trip_id"
	}
}
