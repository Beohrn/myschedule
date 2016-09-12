package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.custom_edit_text.view.*

/**
 * Created by alexkowlew on 08.09.2016.
 */
class CustomEditText : FrameLayout {

  var hint: String = ""
  var inputType = 0

  constructor(context: Context?) : super(context) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    val ta = context!!.obtainStyledAttributes(attrs, R.styleable.CustomEditText, 0, 0)
    hint = ta.getString(R.styleable.CustomEditText_hint)
    inputType = ta.getInt(R.styleable.CustomEditText_inputType, 1)
    ta.recycle()
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    init(context)
  }

  fun init(context: Context?) {
    inflate(context, R.layout.custom_edit_text, this)
    cv_edit_text.hint = hint
    cv_edit_text.inputType = inputType
  }

  fun getText() = cv_edit_text.text.toString()
}