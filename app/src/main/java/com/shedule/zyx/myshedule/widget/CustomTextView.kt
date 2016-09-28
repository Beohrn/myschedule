package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.custom_text_view.view.*

/**
 * Created by alexkowlew on 08.09.2016.
 */
class CustomTextView : FrameLayout {


  var titleText = ""
  var timeText = ""

  constructor(context: Context?) : super(context) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    val ta = context?.obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0)
    titleText = ta?.getString(R.styleable.CustomTextView_title_text) ?: ""
    timeText = ta?.getString(R.styleable.CustomTextView_time_text) ?: ""
    ta?.recycle()
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    init(context)
  }

  fun init(context: Context?) {
    inflate(context, R.layout.custom_text_view, this)
    title_text.text = titleText
    time_text.text = timeText
  }

  fun setText(text: String) { time_text.text = text }
}