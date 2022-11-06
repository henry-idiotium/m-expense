package vn.edu.greenwich.cw_1_sample.ui.resident.list

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_resident_list.*
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.resident.ResidentRegisterFragment
import vn.edu.greenwich.cw_1_sample.ui.resident.ResidentSearchFragment

class ResidentListFragment : Fragment(R.layout.fragment_resident_list), ResidentSearchFragment.FragmentListener {
	private lateinit var _db: ResimaDAO
	private lateinit var _residentAdapter: ResidentAdapter
	private var _residentList = ArrayList<Resident>()

	override fun onAttach(context: Context) {
		super.onAttach(context)

		_db = ResimaDAO(context)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		_residentAdapter = ResidentAdapter(_residentList)

		list_residents.adapter = _residentAdapter
		list_residents.layoutManager = LinearLayoutManager(context)
		DividerItemDecoration(context, LinearLayoutManager(context).orientation).let {
			list_residents.addItemDecoration(it)
		}

		imgbtn_search_reset.setOnClickListener { resetSearch() }
		imgbtn_search.setOnClickListener { showSearchDialog() }
		btn_resident_add.setOnClickListener { showRegisterResidentDialog() }
		text_list_filter.addTextChangedListener(filter())
	}

	override fun onResume() {
		super.onResume()

		reloadList(null)
	}

	override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
		super.onCreateContextMenu(menu, v, menuInfo)
	}

	override fun sendFromResidentSearchFragment(resident: Resident?) {
		reloadList(if (resident?.isEmpty() == false) resident else null)
	}

	private fun reloadList(resident: Resident?) {
		_db.let { _residentList = it.getResidentList(resident, null, false) }
		_residentAdapter.updateList(_residentList)

		// Show "No Resident." message.
		text_empty_notice.visibility = if (_residentList.isEmpty()) View.VISIBLE else View.GONE
	}

	private fun filter(): TextWatcher {
		return object : TextWatcher {
			override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
				_residentAdapter.filter.filter(charSequence.toString())
			}

			override fun afterTextChanged(editable: Editable) {}
		}
	}

	private fun resetSearch() {
		text_list_filter.setText("")
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
