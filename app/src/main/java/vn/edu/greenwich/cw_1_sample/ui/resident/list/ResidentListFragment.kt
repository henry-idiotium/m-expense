package vn.edu.greenwich.cw_1_sample.ui.resident.list

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_resident_list.*
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.resident.ResidentSearchFragment

class ResidentListFragment : Fragment(R.layout.fragment_resident_list), ResidentSearchFragment.FragmentListener {
	private lateinit var _db: ResimaDAO
	private var residentList = ArrayList<Resident>()
	private lateinit var residentAdapter: ResidentAdapter

	override fun onAttach(context: Context) {
		super.onAttach(context)

		_db = ResimaDAO(context)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		residentAdapter = ResidentAdapter(residentList)

		fmResidentListRecyclerView.adapter = residentAdapter
		fmResidentListRecyclerView.layoutManager = LinearLayoutManager(context)
		DividerItemDecoration(context, LinearLayoutManager(context).orientation).let {
			fmResidentListRecyclerView.addItemDecoration(it)
		}

		fmResidentListButtonResetSearch.setOnClickListener { resetSearch() }
		fmResidentListButtonSearch.setOnClickListener { showSearchDialog() }
		fmResidentListFilter.addTextChangedListener(filter())
	}

	override fun onResume() {
		super.onResume()

		reloadList(null)
	}

	private fun reloadList(resident: Resident?) {
		_db.let { residentList = it.getResidentList(resident, null, false) }
		residentAdapter.updateList(residentList)

		// Show "No Resident." message.
		fmResidentListEmptyNotice.visibility = if (residentList.isEmpty()) View.VISIBLE else View.GONE
	}

	private fun filter(): TextWatcher {
		return object : TextWatcher {
			override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
				residentAdapter.filter.filter(charSequence.toString())
			}
			override fun afterTextChanged(editable: Editable) {}
		}
	}

	private fun resetSearch() {
		fmResidentListFilter.setText("")
		reloadList(null)
	}

	private fun showSearchDialog() {
		ResidentSearchFragment().show(childFragmentManager, null)
	}

	override fun sendFromResidentSearchFragment(resident: Resident?) {
		reloadList(if (resident?.isEmpty() == false) resident else null)
	}
}
