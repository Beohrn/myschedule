package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.no_internet_connection.view.*

/**
 * Created by alexkowlew on 18.10.2016.
 */
class NoConnectionView: FrameLayout {

  var titleText: String = ""
  var descriptionText: String = ""
  constructor(context: Context?) : super(context) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.NoConnectionView, 0, 0)
    titleText = typedArray.getString(R.styleable.NoConnectionView_nc_title)
    descriptionText = typedArray.getString(R.styleable.NoConnectionView_nc_description)
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
    inflate(context, R.layout.no_internet_connection, this)
    no_connection_title.text = titleText
    no_connection_description.text = descriptionText
  }
}