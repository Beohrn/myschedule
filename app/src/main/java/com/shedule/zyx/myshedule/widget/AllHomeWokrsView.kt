package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.models.Schedule
import kotlinx.android.synthetic.main.homework_view_for_all_lessons.view.*

/**
 * Created by alexkowlew on 21.09.2016.
 */
class AllHomeWokrsView : FrameLayout {

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
    inflate(context, R.layout.homework_view_for_all_lessons, this)
  }

  fun setData(schedule: Schedule) {
    lessonName.text = schedule.nameLesson
    var count = 0

    schedule.homework.map { if (it.status) count++ }
    count_performed_tasks.text = count.toString()
    count_unperformed_tasks.text = (schedule.homework.size - count).toString()
  }
}