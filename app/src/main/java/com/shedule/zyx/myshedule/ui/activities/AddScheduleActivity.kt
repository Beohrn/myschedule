package com.shedule.zyx.myshedule.ui.activities

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
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
import com.shedule.zyx.myshedule.models.*
import com.shedule.zyx.myshedule.models.Date
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.add_schedule_activity.*
import kotlinx.android.synthetic.main.add_schedule_screen.*
import kotlinx.android.synthetic.main.number_layout.*
import org.jetbrains.anko.*
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
  var category: Category? = null
  var listOfDates = arrayListOf<String>()

  val FIRST_WEEK = 1
  val SECOND_WEEK = 2
  val BOTH_WEEKS = 0
  var numberOfLesson = 1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(add_schedule_activity)
    ScheduleApplication.getComponent().inject(this)
    setSupportActionBar(add_schedule_toolbar)
    supportActionBar?.title = applicationContext.getString(R.string.add_schedule_toolbar_title)
    add_schedule_toolbar.setTitleTextColor(Color.WHITE)
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    add_schedule_toolbar.setNavigationOnClickListener { finish() }

    type_and_number_of_lesson.onClick {
      val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
      bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }



    begin_period.onClick { showDateDialog() }

    end_period.onClick {
      showDateDialog()
      switcher = 1
    }

    begin_time.onClick { showTimeDialog() }

    end_time.onClick {
      switcher = 1
      showTimeDialog()
    }

    category = Category.HOME_EXAM

    exam.onClick { setColor(Category.EXAM, 0, R.color.mark_red) }
    course_work.onClick { setColor(Category.COURSE_WORK, 1, R.color.mark_orange) }
    standings.onClick { setColor(Category.STANDINGS, 2, R.color.mark_yellow) }
    home_exam.onClick { setColor(Category.HOME_EXAM, 3, R.color.dark_cyan) }

    spinner_number_of_lesson.onItemSelectedListener {
      onItemSelected { adapterView, view, i, l ->
        numberOfLesson = i + 1
      }
    }

    header_bottom_sheet.onClick {
      val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
      bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    repeat_dates.onClick {
      selector("Когда будет повторяться данное занятие?", listOf("Каждую неделю", "По первой неделе", "По второй неделе")) {
        position ->
        when (position) {
          0 -> { setListOfDates(BOTH_WEEKS) }
          1 -> { setListOfDates(FIRST_WEEK) }
          2 -> { setListOfDates(SECOND_WEEK) }
        }
      }
    }

    categoriesColors()
  }

  private fun setListOfDates(week: Int) {
    listOfDates.addAll(scheduleManager.getScheduleByDate(startPeriod ?: null!!, endPeriod ?: null!!,
        intent.getIntExtra("current_day_of_week", 0), week).map { it })
  }

  private fun setColor(cat: Category, id: Int, color: Int) {
    when (id) {
      0 -> {
        exam.setColor(color)
        course_work.setColor(R.color.nb_mark_orange)
        standings.setColor(R.color.nb_mark_yellow)
        home_exam.setColor(R.color.nb_dark_cyan)
      }
      1 -> {
        exam.setColor(R.color.nb_mark_red)
        course_work.setColor(color)
        standings.setColor(R.color.nb_mark_yellow)
        home_exam.setColor(R.color.nb_dark_cyan)
      }
      2 -> {
        exam.setColor(R.color.nb_mark_red)
        course_work.setColor(R.color.nb_mark_orange)
        standings.setColor(color)
        home_exam.setColor(R.color.nb_dark_cyan)
      }
      3 -> {
        exam.setColor(R.color.nb_mark_red)
        course_work.setColor(R.color.nb_mark_orange)
        standings.setColor(R.color.nb_mark_yellow)
        home_exam.setColor(color)
      }
    }
    category = cat
  }

  fun categoriesColors() {
    exam.setColor(R.color.nb_mark_red)
    course_work.setColor(R.color.nb_mark_orange)
    standings.setColor(R.color.nb_mark_yellow)
    home_exam.setColor(R.color.nb_dark_cyan)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.add_item -> {
        parseDataForSchedule(); return true
      }
      R.id.add_schedule_menu -> {
        startPeriod?.let {
          endPeriod?.let {
            alert {
              customView {
                include<View>(R.layout.calendar_layout) {
                  val calendarView = find<MaterialCalendarView>(R.id.calendarView)

                  calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE
                  calendarView.state().edit()
                      .setMinimumDate(startPeriod?.let { CalendarDay(it.year, it.monthOfYear, it.dayOfMonth) })
                      .setMaximumDate(endPeriod?.let { CalendarDay(it.year, it.monthOfYear, it.dayOfMonth) })
                      .commit()

                  find<TextView>(R.id.clear_dates).onClick { calendarView.clearSelection() }
                  find<TextView>(R.id.cancel_dates).onClick { dismiss() }
                  find<TextView>(R.id.approve_dates).onClick {
                    listOfDates.addAll(calendarView.selectedDates.map { "${it.day}-${it.month}-${it.year}" })
                    dismiss()
                  }
                }
              }
            }.show()
          } ?: toast("Укажите дату окончания занятия")
        } ?: toast("Укажите дату начала занятия")
        return true
      }
      else -> return super.onOptionsItemSelected(item)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.add_schedule_item_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  private fun parseDataForSchedule() {
    startPeriod?.let { start ->
      endPeriod?.let { end ->
        val schedule = Schedule(numberOfLesson.toString(),
            if (name_of_lesson.getText().toString().isEmpty()) "" else name_of_lesson.getText().toString(),
            start, end)

        schedule.location = Location(classroom.getText().toString(), housing.getText().toString())
        schedule.startTime = startTime
        schedule.endTime = endTime
        schedule.teacher = Teacher(name_of_teacher.getText().toString(), name_of_lesson.getText().toString())
        schedule.teacher?.assessment = "E"
        schedule.typeLesson = if (spinner_type_of_lesson.selectedItem.toString().equals("Практика")) TypeLesson.SEMINAR else TypeLesson.LECTURE
        schedule.category = category
        schedule.dates.addAll(listOfDates.map { it })

        schedule.dates.let {
          scheduleManager.globalList.add(schedule)
          setResult(Activity.RESULT_OK)
          finish()
        }
      } ?: toast("Упс! Введите окончание периода предмета ")
    } ?: toast("Упс! Введите начало периода предмета")
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
        end_time.setText(time)
        switcher = 0
        endTime = Time(hourOfDay, minute)
      }
      else -> {
        begin_time.setText(time)
        startTime = Time(hourOfDay, minute)
      }
    }
  }

  override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
    val day = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
    val month = if (monthOfYear < 9) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
    val date = "$day.$month.$year"
    when (switcher) {
      1 -> {
        end_period.setText(date)
        switcher = 0
        endPeriod = Date(dayOfMonth, monthOfYear, year)
      }
      else -> {
        begin_period.setText(date)
        startPeriod = Date(dayOfMonth, monthOfYear, year)
      }
    }
  }

}