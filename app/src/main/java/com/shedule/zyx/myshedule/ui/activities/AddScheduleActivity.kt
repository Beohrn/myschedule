package com.shedule.zyx.myshedule.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.add_schedule_activity
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.add_item_view.*
import kotlinx.android.synthetic.main.add_schedule_activity.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.include
import org.jetbrains.anko.onClick
import java.util.*
import javax.inject.Inject

/**
 * Created by alexkowlew on 26.08.2016.
 */
class AddScheduleActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

  @Inject
  lateinit var scheduleManager: ScheduleManager

  var switcher = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(add_schedule_activity)
    ScheduleApplication.getComponent().inject(this)

    setSupportActionBar(add_schedule_toolbar)
    add_schedule_toolbar.title = applicationContext.getString(R.string.add_schedule_toolbar_title)
    add_schedule_toolbar.setTitleTextColor(Color.WHITE)

    number_of_lesson.onClick {
      alert("Выбери номер и тип пары") {
        customView {
          include<View>(R.layout.number_layout) {

          }
        }
      }.show()
    }

//    start_time_of_lesson.onClick {
//      showDialog()
//    }
//
//    end_time_of_lesson.onClick {
//      switcher = 1
//      showDialog()
//    }

  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.add_item -> {
//        addIntoPreference(); return true
        return true
      }
      else -> return super.onOptionsItemSelected(item)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.add_schedule_item_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }



//  private fun addIntoPreference() {
//    val numberOfLesson = number_of_lesson.selectedItem.toString()
//    val nameOfLesson = name_of_lesson.text.toString()
//    val timeStart = start_time_of_lesson.text.toString()
//    val timeEnd = end_time_of_lesson.text.toString()
//    val nameOfTeacher = name_of_teacher.text.toString()
//    val typeOfLesson = type_of_lesson.selectedItem
//
//    if (!nameOfLesson.equals("") || !timeStart.equals("") || !timeEnd.equals("")) {
//      val schedule = Schedule(numberOfLesson, nameOfLesson, Time(timeStart, timeEnd))
//      schedule.teacher = nameOfTeacher
//      schedule.typeLesson = if (typeOfLesson.equals("Лекция")) TypeLesson.LECTURE else TypeLesson.SEMINAR
//      scheduleManager.globalList.add(schedule)
//      scheduleManager.saveSchedule()
//      finish()
//    } else {
//      toast("Enter values")
//    }
//  }

  private fun showDialog() {
    val now = Calendar.getInstance()
    val dialog = TimePickerDialog.newInstance(
        this@AddScheduleActivity,
        now.get(Calendar.HOUR_OF_DAY),
        now.get(Calendar.MINUTE),
        true)
    dialog.accentColor = Color.RED
    dialog.show(fragmentManager, "")
  }

  override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int, second: Int) {
    val h = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
    val m = if (minute < 10) "0$minute" else "$minute"
    val time = "$h:$m"
    when (switcher) {
//      1 -> {
//        end_time_of_lesson.text = time
//        switcher = 0
//      }
//      else -> start_time_of_lesson.text = time
    }

  }
}