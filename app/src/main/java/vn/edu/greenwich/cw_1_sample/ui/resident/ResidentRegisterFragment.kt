package vn.edu.greenwich.cw_1_sample.ui.resident

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_resident_register.*
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.dialog.CalendarFragment
import vn.edu.greenwich.cw_1_sample.utils.serializable

open class ResidentRegisterFragment :
	Fragment(R.layout.fragment_resident_register),
	ResidentRegisterConfirmFragment.FragmentListener,
	CalendarFragment.FragmentListener {

	private lateinit var _db: ResimaDAO

	override fun onAttach(context: Context) {
		super.onAttach(context)

		_db = ResimaDAO(getContext())
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Show Calendar for choosing a date.
		fmResidentRegisterStartDate.setOnTouchListener(fun(_: View, event: MotionEvent): Boolean = showCalendar(event))

		// Create new resident.
		fmResidentRegisterButton.setOnClickListener { register() }

		// what's this for ?
		// Update current resident.
		if (arguments != null) {
			val resident = requireArguments().serializable<Resident>(ARG_PARAM_RESIDENT)

			resident?.name?.let { fmResidentRegisterName.setText(it) }
			resident?.startDate.let { fmResidentRegisterStartDate.setText(it) }
			fmResidentRegisterOwner.isChecked = (resident?.owner == 1)
			fmResidentRegisterButton.setText(R.string.label_update)

			resident?.id?.run { fmResidentRegisterButton.setOnClickListener { update(this) } }
		}
	}

	private fun register() {
		if (!isValidForm()) return moveButton()

		val resident = getResidentFromInput(-1)
		ResidentRegisterConfirmFragment(resident).show(childFragmentManager, null)
	}

	private fun update(id: Long) {
		if (isValidForm()) {
			val resident = getResidentFromInput(id)
			val status = _db.updateResident(resident)

			status.let { (parentFragment as FragmentListener?)?.sendFromResidentRegisterFragment(it) }

			return
		}
		moveButton()
	}

	private fun showCalendar(event: MotionEvent): Boolean {
		if (event.action != MotionEvent.ACTION_DOWN) return false

		CalendarFragment().show(childFragmentManager, null)
		return true
	}

	private fun getResidentFromInput(id: Long): Resident {
		val name = fmResidentRegisterName.text.toString()
		val startDate = fmResidentRegisterStartDate.text.toString()
		val owner = if (fmResidentRegisterOwner.isChecked) 1 else 0

		return Resident(id, name, startDate, owner)
	}

	private fun isValidForm(): Boolean {
		val name = fmResidentRegisterName.text.toString()
		val startDate = fmResidentRegisterStartDate.text.toString()
		var error = ""

		if (name.trim { it <= ' ' }.isEmpty()) {
			error += "* ${getString(R.string.error_blank_name)}"
			return false
		}
		if (startDate.trim { it <= ' ' }.isEmpty()) {
			error += "* ${getString(R.string.error_blank_start_date)}"
			return false
		}

		fmResidentRegisterError.text = error

		return true
	}

	private fun moveButton() {
		val registerLayout = fmResidentRegisterLinearLayout
		val btnParams = fmResidentRegisterButton.layoutParams as LinearLayout.LayoutParams
		val layoutLeftPadding = registerLayout.paddingLeft
		val layoutRightPadding = registerLayout.paddingRight
		val layoutWidth = registerLayout.width - layoutLeftPadding - layoutRightPadding

		btnParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
		btnParams.topMargin += fmResidentRegisterButton.height
		btnParams.leftMargin = if (btnParams.leftMargin == 0) layoutWidth - fmResidentRegisterButton.width else 0
		fmResidentRegisterButton.layoutParams = btnParams
	}

	override fun sendFromResidentRegisterConfirmFragment(status: Long) {
		if (status.toInt() == -1) {
			Toast.makeText(context, R.string.notification_create_fail, Toast.LENGTH_SHORT).show()
		} else {
			Toast.makeText(context, R.string.notification_create_success, Toast.LENGTH_SHORT).show()
			fmResidentRegisterName.setText("")
			fmResidentRegisterStartDate.setText("")
			fmResidentRegisterName.requestFocus()
		}
	}

	override fun sendFromCalendarFragment(date: String?) = fmResidentRegisterStartDate.setText(date)

	interface FragmentListener {
		fun sendFromResidentRegisterFragment(status: Long)
	}

	companion object {
		const val ARG_PARAM_RESIDENT = "resident"
	}
}
