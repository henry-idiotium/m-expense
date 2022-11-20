@file:Suppress("unused")

package trip.m.expense

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/* ---------------------------------------------------
* Serializable
*/
inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
	Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
	else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
	Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
	else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}

/* ---------------------------------------------------
* Theme
*/
fun Context.isAppDarkThemeOn() =
	resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES

/* ---------------------------------------------------
* Hide keyboard
*/
fun Fragment.hideKeyboard() = view?.let { activity?.hideKeyboard(it) }

fun Activity.hideKeyboard() = hideKeyboard(currentFocus ?: View(this))

fun Context.hideKeyboard(view: View) {
	val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
	inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/* ---------------------------------------------------
* Dialog
*/
fun DialogFragment.setWidthPercent(percentage: Int = 90) {
	val percent = percentage.toFloat() / 100
	val dm = Resources.getSystem().displayMetrics
	val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
	val percentWidth = rect.width() * percent

	dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun DialogFragment.setFullScreen() {
	dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
}

fun DialogFragment.setBackgroundTransparent() = dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

/* ---------------------------------------------------
* Date time
*/
fun getCurrentDate(): LocalDate = LocalDate.now()
fun getDateFromMillis(millis: Long): String? {
	val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
	val datetime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }.time
	return formatter.format(datetime)
}

fun LocalDate.toSimpleDateFormat(pattern: String = "dd/MM/yyyy"): String {
	val formatter = DateTimeFormatter.ofPattern(pattern)
	return this.format(formatter)
}

fun String.toLocalDate(pattern: String = "dd/MM/yyyy"): LocalDate {
	val formatter = DateTimeFormatter.ofPattern(pattern)
	return LocalDate.parse(this, formatter)
}
