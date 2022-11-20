package trip.m.expense.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import trip.m.expense.R
import trip.m.expense.databinding.ViewKeyValueWrapperBinding

class KeyValueWrapperView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
	LinearLayout(context, attrs) {

	private var _binding: ViewKeyValueWrapperBinding
	private var _styledAttrs: TypedArray

	init {
		val view = LayoutInflater.from(context).inflate(R.layout.view_key_value_wrapper, this, true)

		_binding = ViewKeyValueWrapperBinding.bind(view)
		_styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.KeyValueWrapperView, 0, 0).also {

			val leftText = it.getString(R.styleable.KeyValueWrapperView_left_text)
			val rightText = it.getString(R.styleable.KeyValueWrapperView_right_text)

			_binding.textKeyValueWrapperLeft.text = leftText
			_binding.textKeyValueWrapperRight.text = rightText
		}
	}

	fun setValue(value: String?, default: String = "---") {
		_binding.textKeyValueWrapperRight.text = if (value != null && value.isNotBlank()) value else default
	}
}
