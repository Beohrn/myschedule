package com.shedule.zyx.myshedule.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.shedule.zyx.myshedule.R.layout.add_schedule_activity
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.managers.PreferencesManager
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.models.Teacher
import com.shedule.zyx.myshedule.models.Time
import kotlinx.android.synthetic.main.add_schedule_activity.*
import org.jetbrains.anko.onClick
import javax.inject.Inject

/**
 * Created by alexkowlew on 26.08.2016.
 */
class AddScheduleActivity : AppCompatActivity() {

  @Inject
  lateinit var preferencesManager: PreferencesManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(add_schedule_activity)
    ScheduleApplication.getComponent().inject(this)

//        save.onClick {
//            val number = number_lesson.text.toString()
//            val description = description_of_lesson.text.toString()
//            val begin = begin_time.text.toString()
//            val end = end_time.text.toString()
//            val schedule = Schedule(number, description, Time(begin, end))
//
//            preferencesManager.addItem(schedule)
//        }
//
//        get.onClick {
//            Log.i("SSSSSS", "${preferencesManager.getSchedule()}")
//        }
//
//        remove_schedule.onClick {
//            preferencesManager.removeItem(1)
//        }


  }

}