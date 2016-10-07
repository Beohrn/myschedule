package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.utils.Constants.Companion.LECTURE
import com.shedule.zyx.myshedule.utils.Utils
import kotlinx.android.synthetic.main.schedule_item_layout.view.*

/**
 * Created by bogdan on 31.07.16.
 */
class ScheduleItemView : FrameLayout {

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
    (view_number_of_lesson.background as GradientDrawable).setColor(Utils.getColorByCategory(context, schedule.category))
    view_number_of_lesson.text = schedule.numberLesson
    view_title_of_lesson.text = if (schedule.nameLesson.isNullOrBlank()) context.getString(R.string.name_of_lesson) else schedule.nameLesson
    view_name_of_teacher.text = if (schedule.teacher?.teacherName.isNullOrBlank()) context.getString(R.string.name_of_teacher) else schedule.teacher?.teacherName
    view_location.text = "${if (schedule.location?.housing.isNullOrBlank()) context.getString(R.string.housing) else schedule.location?.housing}-" +
        "${if(schedule.location?.classroom.isNullOrBlank()) context.getString(R.string.classroom) else schedule.location?.classroom}"
    view_type_of_lesson.text = if (schedule.typeLesson == LECTURE) context.getString(R.string.lecture) else context.getString(R.string.practice)
    view_time_of_lesson.text = "${schedule.startTime?.toString() ?: context.getString(R.string.begin)} - ${schedule.endTime?.toString() ?: context.getString(R.string.end)}"
  }
}