package vn.edu.greenwich.cw_1_sample.utils

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

/**
 * Call this method (in onActivityCreated or later) to set
 * the width of the dialog to a percentage of the current
 * screen width.
 */
fun DialogFragment.setWidthPercent(percentage: Int = 85) {
	val percent = percentage.toFloat() / 100
	val dm = Resources.getSystem().displayMetrics
	val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
	val percentWidth = rect.width() * percent

	dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}

/**
 * Call this method (in onActivityCreated or later)
 * to make the dialog near-full screen.
 */
fun DialogFragment.setFullScreen() {
	dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun DialogFragment.setBackgroundTransparent() {
	dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}
