package vn.edu.greenwich.cw_1_sample.ui.resident

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_resident_detail.*
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.models.Request
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.dialog.DeleteConfirmFragment
import vn.edu.greenwich.cw_1_sample.ui.request.RequestCreateFragment
import vn.edu.greenwich.cw_1_sample.ui.request.list.RequestListFragment
import vn.edu.greenwich.cw_1_sample.utils.serializable

class ResidentDetailFragment :
	Fragment(R.layout.fragment_resident_detail),
	DeleteConfirmFragment.FragmentListener,
	RequestCreateFragment.FragmentListener,
	MenuProvider {

	private lateinit var _navController: NavController
	private lateinit var _db: ResimaDAO
	private var _resident: Resident? = null

	override fun onAttach(context: Context) {
		super.onAttach(context)

		_db = ResimaDAO(getContext())
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		_navController = requireParentFragment().findNavController()

		btn_request_add.setOnClickListener { showAddRequestFragment() }

		val menuHost: MenuHost = requireActivity()
		menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

		showDetails()
		showRequestList()
	}

	override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.menu_in_detail, menu)
	}

	override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
		when (menuItem.itemId) {
			R.id.menu_resident_detail_update -> showUpdateFragment()
			R.id.menu_resident_detail_delete -> showDeleteConfirmFragment()
			android.R.id.home -> _navController.navigateUp()
		}

		return true
	}

	override fun sendFromDeleteConfirmFragment(status: Int) {
		if (status == 1 && _resident?.id != null && _db.deleteResident(_resident!!.id) > 0) {
			Toast.makeText(context, R.string.notification_delete_success, Toast.LENGTH_SHORT).show()
			view?.let { findNavController(it).navigateUp() }

			return
		}

		Toast.makeText(context, R.string.notification_delete_fail, Toast.LENGTH_SHORT).show()
	}

	override fun sendFromRequestCreateFragment(request: Request?) {
		if (request == null) return

		_resident?.id?.let { request.residentId = it }

		val id = _db.insertRequest(request)
		val text = if (id == -1L) R.string.notification_create_fail else R.string.notification_create_success
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

		reloadRequestList()
	}

	private fun showDetails() {
		var name = getString(R.string.error_not_found)
		var startDate = getString(R.string.error_not_found)
		var owner = getString(R.string.error_not_found)

		if (arguments != null) {
			_resident = requireArguments().serializable(ARG_PARAM_RESIDENT)
			_resident = _resident?.let { _db.getResidentById(it.id) } // Retrieve data from Database.

			_resident?.name?.let { name = it }
			_resident?.startDate?.let { startDate = it }
			owner = if (_resident?.owner == 1) getString(R.string.label_owner) else getString(R.string.label_tenant)
		}

		text_detail_name.text = name
		text_detail_start_date.text = startDate
		text_detail_owner.text = owner
	}

	private fun showRequestList() {
		if (arguments != null) return

		val bundle = Bundle()
		bundle.putSerializable(RequestListFragment.ARG_PARAM_RESIDENT_ID, _resident?.id)

		// Send arguments (resident id) to RequestListFragment.
		childFragmentManager.fragments[0].arguments = bundle
	}

	private fun showUpdateFragment() {
		var bundle: Bundle? = null

		if (_resident != null) {
			bundle = Bundle()
			bundle.putSerializable(ResidentUpdateFragment.ARG_PARAM_RESIDENT, _resident)
		}

		_navController.navigate(R.id.menu_resident_detail_update, bundle)
	}

	private fun showDeleteConfirmFragment() {
		val message = getString(R.string.notification_delete_confirm)

		DeleteConfirmFragment(message).show(childFragmentManager, null)
	}

	private fun showAddRequestFragment() = RequestCreateFragment().show(childFragmentManager, null)

	private fun reloadRequestList() {
		val bundle = Bundle()

		bundle.putSerializable(RequestListFragment.ARG_PARAM_RESIDENT_ID, _resident?.id)
		childFragmentManager.beginTransaction().setReorderingAllowed(true)
			.replace(R.id.list_requests, RequestListFragment::class.java, bundle).commit()
	}

	companion object {
		const val ARG_PARAM_RESIDENT = "resident"
	}
}
