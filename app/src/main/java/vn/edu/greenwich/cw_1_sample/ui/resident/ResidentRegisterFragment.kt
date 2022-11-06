package vn.edu.greenwich.cw_1_sample.ui.resident

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_resident_register.*
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.dialog.CalendarFragment
import vn.edu.greenwich.cw_1_sample.utils.serializable
import vn.edu.greenwich.cw_1_sample.utils.setBackgroundTransparent
import vn.edu.greenwich.cw_1_sample.utils.setWidthPercent

open class ResidentRegisterFragment :
	DialogFragment(R.layout.fragment_resident_register),
	ResidentRegisterConfirmFragment.FragmentListener,
	CalendarFragment.FragmentListener {

	private lateinit var _db: ResimaDAO

	override fun onAttach(context: Context) {
		super.onAttach(context)

		_db = ResimaDAO(getContext())
	}

	override fun onResume() {
		super.onResume()

		setWidthPercent(90)
		setBackgroundTransparent()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Show Calendar for choosing a date.
		text_register_start_date.setOnTouchListener(fun(_: View, event: MotionEvent): Boolean = showCalendar(event))

		// Create new resident.
		btn_register_submit.setOnClickListener { register() }
		btn_register_cancel.setOnClickListener { dismiss() }

		// what's this for ?
		// Update current resident.
		if (arguments != null) {
			val resident = requireArguments().serializable<Resident>(ARG_PARAM_RESIDENT)

			resident?.name?.let { text_register_name.setText(it) }
			resident?.startDate?.let { text_register_start_date.setText(it) }
			checkbox_register_owner.isChecked = (resident?.owner == 1)
			btn_register_submit.setText(R.string.label_update)

			resident?.id?.run { btn_register_submit.setOnClickListener { update(this) } }
		}
	}

	private fun register() {
		if (!isValidForm()) return moveButton()

		val resident = getResidentFromInput(-1)
		ResidentRegisterConfirmFragment(resident).show(childFragmentManager, null)
	}

	private fun update(id: Long) {
		if (!isValidForm()) return moveButton()

		val resident = getResidentFromInput(id)
		val status = _db.updateResident(resident)

		(parentFragment as FragmentListener?)?.sendFromResidentRegisterFragment(status)
	}

	private fun showCalendar(event: MotionEvent): Boolean {
		if (event.action != MotionEvent.ACTION_DOWN) return false

		CalendarFragment().show(childFragmentManager, null)

		return true
	}

	private fun getResidentFromInput(id: Long): Resident {
		val name = text_register_name.text.toString()
		val startDate = text_register_start_date.text.toString()
		val owner = if (checkbox_register_owner.isChecked) 1 else 0

		return Resident(id, name, startDate, owner)
	}

	private fun isValidForm(): Boolean {
		val name = text_register_name.text.toString()
		val date = text_register_start_date.text.toString()
		val errors = mutableListOf<String>()

		if (name.none { !it.isWhitespace() }) errors.add(getString(R.string.error_blank_name))
		if (date.none { !it.isWhitespace() }) errors.add(getString(R.string.error_blank_start_date))

		val isValid: Boolean = errors.isEmpty()

		list_errors.visibility = if (isValid) View.GONE else View.VISIBLE

		if (!isValid) {
			val res = android.R.layout.simple_list_item_1
			list_errors.adapter = ArrayAdapter(requireContext(), res, errors)
		}

		return isValid
	}

	private fun moveButton() {
		val registerLayout = layout_linear
		val btnParams = btn_register_submit.layoutParams as LinearLayout.LayoutParams
		val layoutLeftPadding = registerLayout.paddingLeft
		val layoutRightPadding = registerLayout.paddingRight
		val layoutWidth = registerLayout.width - layoutLeftPadding - layoutRightPadding

		btnParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
		btnParams.topMargin += btn_register_submit.height
		btnParams.leftMargin = if (btnParams.leftMargin == 0) layoutWidth - btn_register_submit.width else 0
		btn_register_submit.layoutParams = btnParams
	}

	override fun sendFromResidentRegisterConfirmFragment(status: Long) {
		if (status.toInt() == -1) {
			Toast.makeText(context, R.string.notification_create_fail, Toast.LENGTH_SHORT).show()
		} else {
			Toast.makeText(context, R.string.notification_create_success, Toast.LENGTH_SHORT).show()
			text_register_name.setText("")
			text_register_start_date.setText("")
			text_register_name.requestFocus()
		}

		dismiss()
	}

	override fun sendFromCalendarFragment(date: String?) = text_register_start_date.setText(date)

	interface FragmentListener {
		fun sendFromResidentRegisterFragment(status: Long)
	}

	companion object {
		const val ARG_PARAM_RESIDENT = "resident"
	}
}
