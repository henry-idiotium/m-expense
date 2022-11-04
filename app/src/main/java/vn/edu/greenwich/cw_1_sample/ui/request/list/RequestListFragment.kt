package vn.edu.greenwich.cw_1_sample.ui.request.list

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_request_list.*
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.models.Request

class RequestListFragment : Fragment(R.layout.fragment_request_list) {
	private var _requestList = ArrayList<Request>()
	private lateinit var _db: ResimaDAO

	override fun onAttach(context: Context) {
		super.onAttach(context)

		_db = ResimaDAO(context)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (arguments != null) {
			val request = Request()
			request.residentId = requireArguments().getLong(ARG_PARAM_RESIDENT_ID)

			_requestList = _db.getRequestList(request, null, false)
		}

		val linearLayoutManager = LinearLayoutManager(context)
		val dividerItemDecoration = DividerItemDecoration(context, linearLayoutManager.orientation)

		fmRequestListRecyclerView.addItemDecoration(dividerItemDecoration)
		fmRequestListRecyclerView.adapter = RequestAdapter(_requestList)
		fmRequestListRecyclerView.layoutManager = LinearLayoutManager(context)

		// Show "No Request." message.
		fmRequestListEmptyNotice.visibility = if (_requestList.isEmpty()) View.VISIBLE else View.GONE
	}

	companion object {
		const val ARG_PARAM_RESIDENT_ID = "resident_id"
	}
}
