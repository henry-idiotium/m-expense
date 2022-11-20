@file:Suppress("UsePropertyAccessSyntax")

package trip.m.expense.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import trip.m.expense.R
import trip.m.expense.databinding.ViewTextInputBinding

class TextInputView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

	private var _binding: ViewTextInputBinding
	private var _styledAttrs: TypedArray
	private val _defTextSize: Float = 32F

	init {
		val view = LayoutInflater.from(context).inflate(R.layout.view_text_input, this, true)

		_binding = ViewTextInputBinding.bind(view)
		_styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TextInputView, 0, 0)

		_styledAttrs.run {
			val text = getString(R.styleable.TextInputView_android_text)
			val label = getString(R.styleable.TextInputView_label)
			val textSize = getDimension(R.styleable.TextInputView_android_textSize, _defTextSize)
			val inputGravity =
				getInt(R.styleable.TextInputView_android_gravity, Gravity.START or Gravity.CENTER_VERTICAL)
			val placeholder = getString(R.styleable.TextInputView_placeholderText)
			val inputType = getInt(R.styleable.TextInputView_android_inputType, InputType.TYPE_CLASS_TEXT)
			val minHeight = getDimension(R.styleable.TextInputView_minHeight, 120F)
			val endIconMode = getInt(R.styleable.TextInputView_endIconMode, 0)
			val textPaddings = getTextPadding()

			_binding.textInput.run {
				setText(text)
				setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
				setPadding(textPaddings.start, textPaddings.top, textPaddings.end, textPaddings.bottom)
				gravity = inputGravity
				this.inputType = inputType
			}
			_binding.layoutTextInput.run {
				this.endIconMode = endIconMode
				placeholder?.let { placeholderText = it }
				minimumHeight = minHeight.toInt()
				label?.let { hint = it }
			}
		}

		_styledAttrs.recycle()
	}

	override fun performClick(): Boolean {
		super.performClick()
		return true
	}

	fun getText() = _binding.textInput.text.toString()
	fun setText(v: String?) = _binding.textInput.setText(v)
	fun setError(message: String) = _binding.textInput.setError(message)
	fun clearError() = _binding.textInput.setError(null)

	fun onEndIconClick(l: OnClickListener) = _binding.layoutTextInput.setEndIconOnClickListener(l)

	fun addTextChangedListener(getWatcher: () -> TextWatcher) = _binding.textInput.addTextChangedListener(getWatcher())

	@SuppressLint("ClickableViewAccessibility")
	fun setInputOnTouchListener(l: OnTouchListener) = _binding.textInput.setOnTouchListener(l)

	fun getTextPadding(): Paddings {
		with(_styledAttrs) {
			val hor = _styledAttrs.getDimension(R.styleable.TextInputView_textPaddingHorizontal, 32F).toInt()
			val ver = _styledAttrs.getDimension(R.styleable.TextInputView_textPaddingVertical, 0F).toInt()

			return Paddings(
				start = hor + getDimension(R.styleable.TextInputView_textPaddingStart, 0F).toInt(),
				end = hor + getDimension(R.styleable.TextInputView_textPaddingEnd, 0F).toInt(),
				top = ver + getDimension(R.styleable.TextInputView_textPaddingTop, 0F).toInt(),
				bottom = ver + getDimension(R.styleable.TextInputView_textPaddingBottom, 0F).toInt()
			)
		}
	}

	inner class Paddings(var start: Int = 0, var end: Int = 0, var top: Int = 0, var bottom: Int = 0)
}
