package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.photo_view.view.*

/**
 * Created by alexkowlew on 15.09.2016.
 */
class PhotoView: FrameLayout {

  constructor(context: Context?) : super(context) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    init(context)
  }

  fun init(context: Context?) {
    inflate(context, R.layout.photo_view, this)
  }

  fun setData(bitmap: Bitmap) {
    image_home_work.setImageBitmap(bitmap)
  }
}