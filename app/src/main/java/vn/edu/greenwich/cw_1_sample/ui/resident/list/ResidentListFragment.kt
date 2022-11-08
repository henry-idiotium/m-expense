package vn.edu.greenwich.cw_1_sample.ui.resident.list

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
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
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.databinding.FragmentResidentListBinding
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.resident.ResidentRegisterFragment
import vn.edu.greenwich.cw_1_sample.ui.resident.ResidentSearchFragment

class ResidentListFragment :
	Fragment(R.layout.fragment_resident_list),
	ResidentSearchFragment.FragmentListener,
	MenuProvider {

	private lateinit var _binding: FragmentResidentListBinding
	private lateinit var _navController: NavController
	private lateinit var _residentAdapter: ResidentAdapter
	private lateinit var _db: ResimaDAO
	private var _residentList = ArrayList<Resident>()

	override fun onAttach(context: Context) {
		super.onAttach(context)

		_db = ResimaDAO(context)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentResidentListBinding.inflate(inflater, container, false)

		_navController = requireParentFragment().findNavController()
		val menuHost = requireActivity()
		menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

		_residentAdapter = ResidentAdapter(_residentList)

		_binding.listResidents.adapter = _residentAdapter
		_binding.listResidents.layoutManager = LinearLayoutManager(context)

		DividerItemDecoration(context, LinearLayoutManager(context).orientation).let {
			_binding.listResidents.addItemDecoration(it)
		}

		_binding.btnSearch.setOnClickListener { showSearchDialog() }
		_binding.btnResidentAdd.setOnClickListener { showRegisterResidentDialog() }
		_binding.textListFilter.addTextChangedListener { filter() }
		_binding.textListFilter.onEndIconChanged { _, _ -> clearSearch() }
		_binding.textListFilter.setOnKeyListener { _, keyCode, event ->
			if (event.action != KeyEvent.ACTION_DOWN) {
				when (keyCode) {
					KeyEvent.KEYCODE_ENTER -> filter()
				}
			}

			true
		}

		return _binding.root
	}

	override fun onResume() {
		super.onResume()

		reloadList(null)
	}

	override fun sendFromResidentSearchFragment(resident: Resident?) {
		reloadList(if (resident?.isEmpty() == false) resident else null)
	}

	override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.menu_resident_list, menu)
	}

	override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
		when (menuItem.itemId) {
			R.id.menu_action_resident_list_reload -> reloadList(null)
			android.R.id.home -> _navController.navigateUp()
		}

		return true
	}

	fun reloadList(resident: Resident?) {
		_residentList = _db.getResidentList(resident, null, false)
		_residentAdapter.updateList(_residentList)

		// Show "No Resident." message.
		_binding.imgEmptyNotice.visibility = if (_residentList.isEmpty()) View.VISIBLE else View.GONE
	}

	private fun filter(): TextWatcher {
		return object : TextWatcher {
			override fun afterTextChanged(editable: Editable) {}
			override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
				_residentAdapter.filter.filter(charSequence.toString())
			}
		}
	}

	private fun clearSearch() {
		_binding.textListFilter.setText("")
		reloadList(null)
	}

	private fun showSearchDialog() {
		ResidentSearchFragment().show(childFragmentManager, null)
	}

	private fun showRegisterResidentDialog() {
		val residentRegisterDialog = ResidentRegisterFragment()

		residentRegisterDialog.show(childFragmentManager, null)
	}
}
