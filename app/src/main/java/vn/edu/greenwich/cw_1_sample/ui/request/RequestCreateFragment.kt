package vn.edu.greenwich.cw_1_sample.ui.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import vn.edu.greenwich.cw_1_sample.databinding.FragmentRequestCreateBinding
import vn.edu.greenwich.cw_1_sample.models.Request
import vn.edu.greenwich.cw_1_sample.ui.dialog.DatePickerFragment
import vn.edu.greenwich.cw_1_sample.ui.dialog.TimePickerFragment
import vn.edu.greenwich.cw_1_sample.utils.setWidthPercent
import android.R as androidR
import vn.edu.greenwich.cw_1_sample.R as appR

class RequestCreateFragment :
	DialogFragment(appR.layout.fragment_request_create),
	DatePickerFragment.FragmentListener,
	TimePickerFragment.FragmentListener {

	private lateinit var _binding: FragmentRequestCreateBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentRequestCreateBinding.inflate(inflater, container, false)

		setTypeSpinner()

		_binding.btnRequestAdd.setOnClickListener { createRequest() }
		_binding.btnRequestCancel.setOnClickListener { dismiss() }
		_binding.textRequestDate.setOnClickListener { showDateDialog() }
		_binding.textRequestTime.setOnClickListener { showTimeDialog() }

		return _binding.root
	}

	override fun onDateChange(date: String) = _binding.textRequestDate.setText(date)

	override fun onTimeChange(time: String) = _binding.textRequestTime.setText(time)

	override fun onResume() {
		super.onResume()

		setWidthPercent()
	}

	private fun setTypeSpinner() {
		val adapter = ArrayAdapter.createFromResource(
			requireContext(),
			appR.array.request_type,
			androidR.layout.simple_spinner_item
		)
		adapter.setDropDownViewResource(androidR.layout.simple_spinner_dropdown_item)
		_binding.spinnerCreateType.adapter = adapter
	}

	private fun showDateDialog() = DatePickerFragment().show(childFragmentManager, null)

	private fun showTimeDialog() = TimePickerFragment().show(childFragmentManager, null)

	private fun createRequest() {
		val request = Request()
		with(Request()) {
			type = _binding.spinnerCreateType.selectedItem.toString()
			time = _binding.spinnerCreateType.selectedItem.toString()
			date = _binding.spinnerCreateType.selectedItem.toString()
			content = _binding.spinnerCreateType.selectedItem.toString()
		}

		(requireParentFragment() as FragmentListener).onCreateRequest(request)
		dismiss()
	}

	interface FragmentListener {
		fun onCreateRequest(request: Request)
	}
}
