package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.empty_content.view.*

/**
 * Created by alexkowlew on 29.09.2016.
 */
class EmptyContentView: FrameLayout {

  var titleText: String = ""
  constructor(context: Context?) : super(context) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.EmptyContentView, 0, 0)
    titleText = typedArray.getString(R.styleable.EmptyContentView_overview_text)
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
    inflate(context, R.layout.empty_content, this)
    empty_content_title.text = titleText
  }
}