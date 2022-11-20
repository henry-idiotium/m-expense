package trip.m.expense.ui.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import trip.m.expense.R

class IconButtonView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = R.attr.appIconButtonStyle,
) : MaterialButton(context, attrs, defStyleAttr)
