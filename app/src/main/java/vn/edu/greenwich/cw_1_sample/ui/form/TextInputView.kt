@file:Suppress("UsePropertyAccessSyntax")

package vn.edu.greenwich.cw_1_sample.ui.form

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputLayout
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.databinding.ViewTextInputBinding

class TextInputView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

	private var _binding: ViewTextInputBinding
	private val _defTextSize: Float = 32F
	private var _styledAttrs: TypedArray

	init {
		val inflater = LayoutInflater.from(context)
		val view = inflater.inflate(R.layout.view_text_input, this, true)

		_binding = ViewTextInputBinding.inflate(inflater, view as ViewGroup)
		_styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TextInputView, 0, 0)

		_styledAttrs.run {
			val text = getString(R.styleable.TextInputView_text)
			val label = getString(R.styleable.TextInputView_label)
			val textSize = getDimension(R.styleable.TextInputView_textSize, _defTextSize)
			val placeholder = getString(R.styleable.TextInputView_placeholder)
			val endIconMode = getInt(R.styleable.TextInputView_inputEndIconMode, 0)
			val priorityUserInput = getBoolean(R.styleable.TextInputView_priorityUserInput, false)
			val textPaddings = getTextPadding(priorityUserInput)

			_binding.textInput.setText(text)
			_binding.textInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
			_binding.textInput.setPadding(textPaddings.start, textPaddings.top, textPaddings.end, textPaddings.bottom)
			placeholder?.let { _binding.layoutTextInput.placeholderText = it }
			label?.let { _binding.layoutTextInput.hint = it }
			_binding.layoutTextInput.endIconMode = endIconMode
		}

		_styledAttrs.recycle()
	}

	fun getText() = _binding.textInput.text.toString()
	fun setText(v: String?) = _binding.textInput.setText(v)

	private fun getContainer(): ViewGroup {
		val parent = this.getParent()

		if (parent !is ViewGroup) {
			val grandparent = parent.getParent()

			if (grandparent is ViewGroup) return grandparent
		}

		return parent as ViewGroup
	}

	fun onEndIconChanged(l: TextInputLayout.OnEndIconChangedListener) {
		_binding.layoutTextInput.addOnEndIconChangedListener(l)
	}

	override fun performClick(): Boolean {
		super.performClick()
		return true
	}

	fun addTextChangedListener(getWatcher: () -> TextWatcher) = _binding.textInput.addTextChangedListener(getWatcher())

	@SuppressLint("ClickableViewAccessibility")
	fun setInputOnTouchListener(l: OnTouchListener) = _binding.textInput.setOnTouchListener(l)

	fun getTextPadding(priorityUserInput: Boolean): Paddings {
		with(_styledAttrs) {
			Paddings().let { p ->
				fun validate(user: Int, relative: Int, def: Int): Int {
					return (user.takeIf { priorityUserInput } ?: def) + relative
				}

				_styledAttrs.getDimension(R.styleable.TextInputView_textPaddingHorizontal, 0F).toInt().let {
					p.start = validate(getDimension(R.styleable.TextInputView_textPaddingStart, 0F).toInt(), it, 0)
					p.end = validate(getDimension(R.styleable.TextInputView_textPaddingEnd, 0F).toInt(), it, 0)
				}
				_styledAttrs.getDimension(R.styleable.TextInputView_textPaddingVertical, 0F).toInt().let {
					p.top = validate(getDimension(R.styleable.TextInputView_textPaddingTop, 0F).toInt(), it, 60)
					p.bottom = validate(getDimension(R.styleable.TextInputView_textPaddingBottom, 0F).toInt(), it, 20)
				}

				return p
			}
		}
	}

	inner class Paddings(
		var start: Int = 0,
		var end: Int = 0,
		var top: Int = 0,
		var bottom: Int = 0,
	)
}
