package vn.edu.greenwich.cw_1_sample.ui.request.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.databinding.FragmentRequestListBinding
import vn.edu.greenwich.cw_1_sample.models.Request

class RequestListFragment : Fragment(R.layout.fragment_request_list) {

	private lateinit var _binding: FragmentRequestListBinding
	private lateinit var _db: ResimaDAO
	private var _requestList = ArrayList<Request>()

	override fun onAttach(context: Context) {
		super.onAttach(context)

		_db = ResimaDAO(context)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentRequestListBinding.inflate(inflater, container, false)

		if (arguments != null) {
			val request = Request()
			request.residentId = requireArguments().getLong(ARG_PARAM_RESIDENT_ID)

			_requestList = _db.getRequestList(request, null, false)
		}

		val linearLayoutManager = LinearLayoutManager(context)
		val dividerItemDecoration = DividerItemDecoration(context, linearLayoutManager.orientation)

		_binding.listRequests.addItemDecoration(dividerItemDecoration)
		_binding.listRequests.adapter = RequestAdapter(_requestList)
		_binding.listRequests.layoutManager = LinearLayoutManager(context)

		// Show "No Request." message.
		_binding.textEmptyNotice.visibility = if (_requestList.isEmpty()) View.VISIBLE else View.GONE

		return _binding.root
	}

	companion object {
		const val ARG_PARAM_RESIDENT_ID = "resident_id"
	}
}
