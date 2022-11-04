package vn.edu.greenwich.cw_1_sample.ui.request

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_request_create.*
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

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setTypeSpinner()

		fmRequestCreateButtonAdd.setOnClickListener { createRequest() }
		fmRequestCreateButtonCancel.setOnClickListener { dismiss() }
		fmRequestCreateDate.setOnTouchListener(fun(_: View?, motionEvent: MotionEvent): Boolean {
			return showDateDialog(motionEvent)
		})
		fmRequestCreateTime.setOnTouchListener(fun(_: View?, motionEvent: MotionEvent): Boolean {
			return showTimeDialog(motionEvent)
		})
	}

	override fun sendFromDatePickerFragment(date: String?) = fmRequestCreateDate.setText(date)

	override fun sendFromTimePickerFragment(time: String?) = fmRequestCreateTime.setText(time)

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
		fmRequestCreateType.adapter = adapter
	}

	private fun showDateDialog(motionEvent: MotionEvent): Boolean {
		if (motionEvent.action == MotionEvent.ACTION_DOWN) {
			DatePickerFragment().show(childFragmentManager, null)
			return true
		}

		return false
	}

	private fun showTimeDialog(motionEvent: MotionEvent): Boolean {
		if (motionEvent.action == MotionEvent.ACTION_DOWN) {
			TimePickerFragment().show(childFragmentManager, null)
			return true
		}

		return false
	}

	private fun createRequest() {
		val request = Request()
		with(request) {
			type = fmRequestCreateType.selectedItem.toString()
			time = fmRequestCreateType.selectedItem.toString()
			date = fmRequestCreateType.selectedItem.toString()
			content = fmRequestCreateType.selectedItem.toString()
		}

		(parentFragment as FragmentListener?)?.sendFromRequestCreateFragment(request)
		dismiss()
	}

	interface FragmentListener {
		fun sendFromRequestCreateFragment(request: Request?)
	}
}
