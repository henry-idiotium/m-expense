package trip.m.expense.ui.trip.list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import trip.m.expense.R
import trip.m.expense.database.DbDAO
import trip.m.expense.databinding.FragmentTripListBinding
import trip.m.expense.models.Trip
import trip.m.expense.ui.trip.TripCreateFragment
import trip.m.expense.ui.trip.TripSearchFragment

class TripListFragment :
	Fragment(R.layout.fragment_trip_list),
	TripSearchFragment.FragmentListener,
	TripCreateFragment.FragmentListener,
	MenuProvider {

	private lateinit var _binding: FragmentTripListBinding
	private lateinit var _navController: NavController
	private lateinit var _tripAdapter: TripAdapter
	private lateinit var _db: DbDAO.TripDbSet
	private var _tripList = ArrayList<Trip>()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentTripListBinding.inflate(inflater, container, false)
		_db = DbDAO(context).TripDbSet()

		_navController = requireParentFragment().findNavController()
		_tripAdapter = TripAdapter(_tripList)
		_binding.listTrips.adapter = _tripAdapter
		_binding.listTrips.layoutManager = LinearLayoutManager(context)

		requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
		setChildViews()

		return _binding.root
	}

	private fun setChildViews() = with(_binding) {
		btnSearch.setOnClickListener { showSearchDialog() }
		btnResidentAdd.setOnClickListener { showTripCreateDialog() }
		textListFilter.addTextChangedListener { filter() }
		textListFilter.onEndIconClick { clearSearch() }
		listTrips.addItemDecoration(
			DividerItemDecoration(context, LinearLayoutManager(context).orientation)
		)
	}

	override fun onResume() = super.onResume().also { reloadList() }
	override fun onTripSearch(trip: Trip?) = reloadList(trip)
	override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.menu_trip_list, menu)
	}

	override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
		when (menuItem.itemId) {
			R.id.menu_action_trip_list_reload -> reloadList()
			android.R.id.home -> _navController.navigateUp()
		}

		return true
	}

	override fun onTripCreated(tripId: Long) = reloadList()

	private fun reloadList(trip: Trip? = null) {
		_tripList = _db.get(trip)
		_tripAdapter.update(_tripList)

		_binding.listTrips.visibility = if (_tripList.isEmpty()) View.GONE else View.VISIBLE
		_binding.imgEmptyNotice.visibility = if (_tripList.isEmpty()) View.VISIBLE else View.GONE
	}

	private fun filter(): TextWatcher = object : TextWatcher {
		override fun afterTextChanged(editable: Editable) {}
		override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
		override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
			reloadList()
			val tripFilter = _tripAdapter.filter
			tripFilter.filter(charSequence.toString())
		}
	}

	private fun clearSearch() {
		_binding.textListFilter.setText("")
		reloadList()
	}

	private fun showSearchDialog() {
		TripSearchFragment().show(childFragmentManager, null)
	}

	private fun showTripCreateDialog() {
		TripCreateFragment().show(childFragmentManager, null)
	}
}
