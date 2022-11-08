package vn.edu.greenwich.cw_1_sample.ui.resident

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.databinding.FragmentResidentRegisterConfirmBinding
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.resident.list.ResidentListFragment
import vn.edu.greenwich.cw_1_sample.utils.setWidthPercent

class ResidentRegisterConfirmFragment(var resident: Resident? = null) :
	DialogFragment(R.layout.fragment_resident_register_confirm) {

	private lateinit var _binding: FragmentResidentRegisterConfirmBinding
	private lateinit var _db: ResimaDAO
	private var _resident: Resident = resident ?: Resident()

	override fun onAttach(context: Context) {
		super.onAttach(context)

		_db = ResimaDAO(getContext())
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentResidentRegisterConfirmBinding.inflate(inflater, container, false)

		setWidthPercent()

		_binding.btnCancel.setOnClickListener { dismiss() }
		_binding.btnConfirm.setOnClickListener {
			confirm()
		}

		_binding.textConfirmName.text = when {
			_resident.name?.trim()?.isNotEmpty() == false -> getString(R.string.error_no_info)
			else -> _resident.name
		}
		_binding.textConfirmStartDate.text = when {
			_resident.startDate?.trim()?.isNotEmpty() == false -> getString(R.string.error_no_info)
			else -> _resident.startDate
		}
		_binding.textConfirmOwner.text = when (_resident.owner) {
			-1 -> getString(R.string.error_no_info)
			1 -> getString(R.string.label_owner)
			else -> getString(R.string.label_tenant)
		}

		return _binding.root
	}

	private fun confirm() {
		val listFragment = requireParentFragment().requireParentFragment()
		val status = _db.insertResident(_resident)

		// reload residents list
		listFragment.let { if (it is ResidentListFragment) it.reloadList(null) }

		status.let { (requireParentFragment() as FragmentListener).sendFromResidentRegisterConfirmFragment(it) }
		dismiss()
	}

	interface FragmentListener {
		fun sendFromResidentRegisterConfirmFragment(status: Long)
	}
}
