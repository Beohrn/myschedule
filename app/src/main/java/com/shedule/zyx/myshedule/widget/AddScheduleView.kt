package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import com.shedule.zyx.myshedule.R
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

/**
 * Created by bogdan on 31.07.16.
 */
class AddScheduleView : CardView {

  constructor(context: Context?) : super(context) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context)
  }

  fun init(context: Context?) {
    inflate(context, R.layout.add_schedule_layout, this)
  }

  init {
    onClick { context.toast("hello") }
  }
}