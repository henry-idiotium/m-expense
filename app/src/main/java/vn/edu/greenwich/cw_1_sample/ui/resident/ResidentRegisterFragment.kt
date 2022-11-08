package vn.edu.greenwich.cw_1_sample.ui.resident

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.database.ResimaDAO
import vn.edu.greenwich.cw_1_sample.databinding.FragmentResidentRegisterBinding
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.dialog.CalendarFragment
import vn.edu.greenwich.cw_1_sample.utils.serializable
import vn.edu.greenwich.cw_1_sample.utils.setBackgroundTransparent
import vn.edu.greenwich.cw_1_sample.utils.setWidthPercent

open class ResidentRegisterFragment :
	DialogFragment(R.layout.fragment_resident_register),
	ResidentRegisterConfirmFragment.FragmentListener,
	CalendarFragment.FragmentListener {

	private lateinit var _binding: FragmentResidentRegisterBinding
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

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = FragmentResidentRegisterBinding.inflate(inflater, container, false)

		// Show Calendar for choosing a date.
		_binding.textRegisterStartDate.setInputOnTouchListener { v, event ->
			v.performClick()

			showCalendar(event) || v.onTouchEvent(event)
		}

		// Create new resident.
		_binding.btnRegisterSubmit.setOnClickListener { register() }
		_binding.btnRegisterCancel.setOnClickListener { dismiss() }

		// what's this for ?
		// Update current resident.
		if (arguments != null) {
			val resident = requireArguments().serializable<Resident>(ARG_PARAM_RESIDENT)

			resident?.name?.let { _binding.textRegisterName.setText(it) }
			resident?.startDate?.let { _binding.textRegisterStartDate.setText(it) }
			_binding.checkboxRegisterOwner.isChecked = (resident?.owner == 1)
			_binding.btnRegisterSubmit.setText(R.string.label_update)

			resident?.id?.run { _binding.btnRegisterSubmit.setOnClickListener { update(this) } }
		}

		return _binding.root
	}

	private fun register() {
		if (!isValidForm()) return

		val resident = getResidentFromInput(-1)
		ResidentRegisterConfirmFragment(resident).show(childFragmentManager, null)
	}

	private fun update(id: Long) {
		if (!isValidForm()) return

		val resident = getResidentFromInput(id)
		val status = _db.updateResident(resident)

		(requireParentFragment() as FragmentListener).sendFromResidentRegisterFragment(status)
	}

	private fun showCalendar(event: MotionEvent): Boolean {
		if (event.action != MotionEvent.ACTION_DOWN) return false

		CalendarFragment().show(childFragmentManager, null)

		return true
	}

	private fun getResidentFromInput(id: Long): Resident {
		val name = _binding.textRegisterName.getText()
		val startDate = _binding.textRegisterStartDate.getText()
		val owner = if (_binding.checkboxRegisterOwner.isChecked) 1 else 0

		return Resident(id, name, startDate, owner)
	}

	private fun isValidForm(): Boolean {
		val name = _binding.textRegisterName.getText()
		val date = _binding.textRegisterStartDate.getText()
		val errors = mutableListOf<String>()

		if (name.none { !it.isWhitespace() }) errors.add(getString(R.string.error_blank_name))
		if (date.none { !it.isWhitespace() }) errors.add(getString(R.string.error_blank_start_date))

		val isValid: Boolean = errors.isEmpty()

		_binding.listErrors.visibility = if (isValid) View.GONE else View.VISIBLE

		if (!isValid) {
			val res = android.R.layout.simple_list_item_1
			_binding.listErrors.adapter = ArrayAdapter(requireContext(), res, errors)
		}

		return isValid
	}

	override fun sendFromResidentRegisterConfirmFragment(status: Long) {
		val isValid = status.toInt() != -1
		val messageId = if (isValid) R.string.notification_create_fail else R.string.notification_create_success

		if (isValid) {
			_binding.textRegisterName.setText("")
			_binding.textRegisterStartDate.setText("")
			_binding.textRegisterName.requestFocus()
		}

		Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
		dismiss()
	}

	override fun onCalendarChange(date: String) = _binding.textRegisterStartDate.setText(date)

	interface FragmentListener {
		fun sendFromResidentRegisterFragment(status: Long)
	}

	companion object {
		const val ARG_PARAM_RESIDENT = "resident"
	}
}
