package vn.edu.greenwich.cw_1_sample.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import vn.edu.greenwich.cw_1_sample.R

class DeleteConfirmFragment(message: String) : DialogFragment(R.layout.fragment_delete_confirm) {
	private var _message = message

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		super.onCreateDialog(savedInstanceState)

		return AlertDialog.Builder(context)
			.setNegativeButton(R.string.label_cancel) { _, _ -> dismiss() }
			.setPositiveButton(R.string.label_confirm) { _, _ -> delete() }
			.setTitle(R.string.label_confirmation)
			.setMessage(_message).create()
	}

	private fun delete() {
		(requireParentFragment() as FragmentListener).onDeleteConfirm(1)
		dismiss()
	}

	interface FragmentListener {
		fun onDeleteConfirm(status: Int)
	}
}
