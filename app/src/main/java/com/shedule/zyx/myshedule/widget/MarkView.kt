package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.mark_layout.view.*

/**
 * Created by alexkowlew on 30.08.2016.
 */
class MarkView : FrameLayout {

  var color: Int = 0
  var text: String = ""

  constructor(context: Context?) : super(context) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.MarkView, 0, 0)

    color = typedArray.getColor(R.styleable.MarkView_color_mark, R.color.dark_cyan)
    text = typedArray.getString(R.styleable.MarkView_text)
    typedArray.recycle()
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    init(context)
  }

  fun init(context: Context?) {
    inflate(context, R.layout.mark_layout, this)
    (mark_view.background as GradientDrawable).setColor(color)
    mark_view.text = text
  }

  fun setColor(color: Int?) {
    (mark_view.background as GradientDrawable).setColor(resources.getColor(color ?: R.color.dark_cyan))
  }

  fun setTextCategory(text: String?) {
    mark_view.text = text ?: ""
  }

}