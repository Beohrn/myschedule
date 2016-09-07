package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.ScrollView
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.models.TypeLesson
import com.shedule.zyx.myshedule.utils.Utils
import kotlinx.android.synthetic.main.schedule_dialog_layout.view.*
import kotlinx.android.synthetic.main.schedule_item_layout.view.*
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

  fun setData(schedule: Schedule) {
    (view_number_of_lesson.background as GradientDrawable).setColor(Utils.getColorByCategory(context, schedule.category!!))
    view_number_of_lesson.text = schedule.numberLesson
    view_title_of_lesson.text = schedule.nameLesson
    view_name_of_teacher.text = schedule.teacher?.nameOfTeacher ?: "Имя преподавателя"
    view_location.text = "${schedule.location?.housing}-${schedule.location?.classroom}"
    view_type_of_lesson.text = if (schedule.typeLesson == TypeLesson.LECTURE) "Лекция" else "Практика"
    view_time_of_lesson.text = "${schedule.startTime?.toString() ?: "Начало"} - ${schedule.endTime?.toString() ?: "Конец"}"
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