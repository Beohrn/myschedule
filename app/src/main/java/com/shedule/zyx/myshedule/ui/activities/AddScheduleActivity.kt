package com.shedule.zyx.myshedule.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.add_schedule_activity
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.models.Date
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.models.Time
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.add_item_view.*
import kotlinx.android.synthetic.main.add_schedule_activity.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.include
import org.jetbrains.anko.onClick
import java.util.*
import javax.inject.Inject

/**
 * Created by alexkowlew on 26.08.2016.
 */
class AddScheduleActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

  @Inject
  lateinit var scheduleManager: ScheduleManager

  var switcher = 0
  var startPeriod: Date? = null
  var endPeriod: Date? = null
  var startTime: Time? = null
  var endTime: Time? = null
  var listOfDates = arrayListOf<CalendarDay>()

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

    start_period_of_lesson.onClick { showDateDialog() }

    end_period_of_lesson.onClick {
      showDateDialog()
      switcher = 1
    }

    start_time_of_lesson.onClick { showTimeDialog() }

    end_time_of_lesson.onClick {
      switcher = 1
      showTimeDialog()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.add_item -> {
        addIntoPreference(); return true
      }
      R.id.additional_calendar -> {
        alert {
          customView {
            include<View>(R.layout.calendar_layout) {
              val calendarView = find<MaterialCalendarView>(R.id.calendarView)
              calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE
              find<TextView>(R.id.clear_dates).onClick { calendarView.clearSelection() }
              find<TextView>(R.id.cancel_dates).onClick { dismiss() }
              find<TextView>(R.id.approve_dates).onClick {
                listOfDates.addAll(calendarView.selectedDates)
                dismiss()
              }
            }
          }
        }.show()
        return true
      }
      else -> return super.onOptionsItemSelected(item)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.add_schedule_item_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }


  private fun addIntoPreference() {
    val nameOfLesson = name_of_lesson.text.toString()
    val schedule = Schedule(1, nameOfLesson, startTime!!, endTime!!)
    scheduleManager.globalList.add(schedule)
    finish()

  }

  private fun showTimeDialog() {
    val now = Calendar.getInstance()
    val dialog = TimePickerDialog.newInstance(
        this@AddScheduleActivity,
        now.get(Calendar.HOUR_OF_DAY),
        now.get(Calendar.MINUTE),
        true)
    dialog.accentColor = Color.RED
    dialog.show(fragmentManager, "")
  }

  private fun showDateDialog() {
    val now = Calendar.getInstance()
    val dialog = DatePickerDialog.newInstance(
        this@AddScheduleActivity,
        now.get(Calendar.YEAR),
        now.get(Calendar.MONTH),
        now.get(Calendar.DAY_OF_MONTH))
    dialog.accentColor = Color.BLUE
    dialog.show(fragmentManager, "")
  }

  override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int, second: Int) {
    val h = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
    val m = if (minute < 10) "0$minute" else "$minute"
    val time = "$h:$m"
    when (switcher) {
      1 -> {
        end_time_of_lesson.text = time
        switcher = 0
        endTime = Time(hourOfDay, minute)
      }
      else -> {
        start_time_of_lesson.text = time
        startTime = Time(hourOfDay, minute)
      }
    }
  }

  override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
    val day = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
    val month = if (monthOfYear < 10) "0$monthOfYear" else "$monthOfYear"
    val date = "$day.$month.$year"
    when (switcher) {
      1 -> {
        end_period_of_lesson.text = date
        switcher = 0
        endPeriod = Date(dayOfMonth, monthOfYear, year)
      }
      else -> {
        start_period_of_lesson.text = date
        startPeriod = Date(dayOfMonth, monthOfYear, year)
      }
    }
  }

}