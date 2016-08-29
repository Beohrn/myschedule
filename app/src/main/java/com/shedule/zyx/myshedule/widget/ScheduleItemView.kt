package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.ScrollView
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.schedule_dialog_layout.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.include
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

/**
 * Created by bogdan on 31.07.16.
 */
class ScheduleItemView : FrameLayout, ClickListener {

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
    inflate(context, R.layout.schedule_item_layout, this)
  }

  override fun onClick() {
    context.alert {
      customView {
        include<View>(R.layout.schedule_dialog_layout) {
          add_photo.onClick { context.toast("dvsdv") }
          add_task.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
              val view = TaskView(context)
              view.setText(true, add_task.text.toString())
              add_task.setText("")
              container.addView(view)

              post { scroll_view.fullScroll(ScrollView.FOCUS_DOWN) }
              true
            } else false
          }
        }
      }
    }.show()
  }
}

interface ClickListener {
  fun onClick()
}