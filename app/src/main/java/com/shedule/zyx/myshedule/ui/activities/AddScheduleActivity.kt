package com.shedule.zyx.myshedule.ui.activities

import android.app.Activity
import android.content.pm.ActivityInfo
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
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.add_schedule_activity
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.models.*
import com.shedule.zyx.myshedule.models.Category.*
import com.shedule.zyx.myshedule.models.Date
import com.shedule.zyx.myshedule.utils.Utils
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.add_schedule_activity.*
import kotlinx.android.synthetic.main.add_schedule_screen.*
import kotlinx.android.synthetic.main.number_layout.*
import org.jetbrains.anko.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

/**
 * Created by alexkowlew on 26.08.2016.
 */
class AddScheduleActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

  @Inject
  lateinit var scheduleManager: ScheduleManager

  @Inject
  lateinit var firebaseWrapper: FirebaseWrapper

  companion object {
    val ADD_SCHEDULE_REQUEST = 5555
    val EDIT_SCHEDULE_REQUEST = 5423
    val DAY_OF_WEEK_KEY = "current_day_of_week"
  }

  var switcher = 0
  var startPeriod: Date? = null
  var endPeriod: Date? = null
  var startTime: Time? = null
  var endTime: Time? = null
  var category: Category? = null
  var listOfDates = arrayListOf<String>()
  var week = 0

  val FIRST_WEEK = 1
  val SECOND_WEEK = 2
  val BOTH_WEEKS = 0
  val RANDOM_DATES = 3
  var numberOfLesson = 1
  var isScheduleEdit = false
  var schedule: Schedule? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(add_schedule_activity)
    ScheduleApplication.getComponent().inject(this)
    setSupportActionBar(add_schedule_toolbar)
    supportActionBar?.title = getString(R.string.add_schedule_toolbar_title)
    add_schedule_toolbar.setTitleTextColor(Color.WHITE)

    category = SIMPLE_LESSON

    categoriesColors()
    setPeriods(Date(1, 8, 2016), Date(31, 11, 2016))
    setTime(Time(8, 30), Time(10, 5))
    schedule = scheduleManager.editSchedule

    schedule?.let {
      isScheduleEdit = true
      supportActionBar?.title = getString(R.string.set_lesson)
      setPeriods(it.startPeriod, it.endPeriod)
      setListOfDates(it.week)
      numberOfLesson = it.numberLesson.toInt()

      housing.setText("${it.location?.housing}")
      classroom.setText("${it.location?.classroom}")

      spinner_number_of_lesson.setSelection(it.numberLesson.toInt() - 1)
      spinner_type_of_lesson.setSelection(if (it.typeLesson == TypeLesson.SEMINAR) 0 else 1)

      when (it.category) {
        EXAM -> setColor(EXAM, 0, R.color.mark_red)
        COURSE_WORK -> setColor(COURSE_WORK, 1, R.color.mark_orange)
        STANDINGS -> setColor(STANDINGS, 2, R.color.mark_yellow)
        HOME_EXAM -> setColor(HOME_EXAM, 3, R.color.dark_cyan)
        SIMPLE_LESSON -> setColor(SIMPLE_LESSON, 4, R.color.silver)
      }

      name_of_lesson.setText(it.nameLesson)
      name_of_teacher.setText(it.teacher?.teacherName.toString())

      setTime(it.startTime, it.endTime)
    }

    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    add_schedule_toolbar.setNavigationOnClickListener { finish() }

    type_and_number_of_lesson.onClick {
      val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
      bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    begin_period.onClick { showDateDialog(); switcher = 1 }
    teachers_list.onClick {
      val dialog = indeterminateProgressDialog(getString(R.string.load))
      firebaseWrapper.getTeachers()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({ teachers ->
            dialog.dismiss()
            if (teachers != null) {
              selector(null, teachers.map { it.teacherName }) {
                name_of_teacher.setText(teachers[it].teacherName)
              }
            } else toast(getString(R.string.notting_teachers))
          }, {
            dialog.dismiss()
          })
    }

    begin_period.onClick { showDateDialog(); switcher = 1 }

    end_period.onClick { showDateDialog(); switcher = 2 }

    begin_time.onClick { showTimeDialog(); switcher = 3 }

    end_time.onClick { showTimeDialog(); switcher = 4 }

    exam.onClick { setColor(EXAM, 0, R.color.mark_red) }
    course_work.onClick { setColor(COURSE_WORK, 1, R.color.mark_orange) }
    standings.onClick { setColor(STANDINGS, 2, R.color.mark_yellow) }
    home_exam.onClick { setColor(HOME_EXAM, 3, R.color.dark_cyan) }
    simple_lesson.onClick { setColor(SIMPLE_LESSON, 4, R.color.silver) }

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
      startPeriod?.let {
        endPeriod?.let {
          selector(null,
              listOf(getString(R.string.every_week),
                  getString(R.string.on_first_week),
                  getString(R.string.on_second_week),
                  getString(R.string.random_dates))) {
            position ->
            when (position) {
              0 -> { setListOfDates(BOTH_WEEKS) }
              1 -> { setListOfDates(FIRST_WEEK) }
              2 -> { setListOfDates(SECOND_WEEK) }
              3 -> {
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
                        if (listOfDates.size != 0)
                          listOfDates.clear()

                        listOfDates.addAll(calendarView.selectedDates.map { "${it.day}-${it.month}-${it.year}" })
                        setListOfDates(RANDOM_DATES)
                        dismiss()
                      }
                    }
                  }
                }.show()
              }
            }
          }
        } ?: toast(getString(R.string.set_date_of_end_period))
      } ?: toast(getString(R.string.set_date_of_begin_period))
    }
  }

  private fun setPeriods(startPeriod: Date, endPeriod: Date) {
    begin_period.setText(Utils.getNormalizedDate(startPeriod))
    end_period.setText(Utils.getNormalizedDate(endPeriod))

    this.startPeriod = startPeriod
    this.endPeriod = endPeriod
    setListOfDates(BOTH_WEEKS)
  }

  private fun setTime(startTime: Time?, endTime: Time?) {
    startTime?.let {
      begin_time.setText(it.toString())
    } ?: begin_time.setText(getString(R.string.time_reduction))

    endTime?.let {
      end_time.setText(it.toString())
    } ?: begin_time.setText(getString(R.string.time_reduction))

    this.startTime = startTime
    this.endTime = endTime
  }

  override fun onDestroy() {
    super.onDestroy()
    scheduleManager.editSchedule = null
  }

  private fun setListOfDates(week: Int) {
    if (listOfDates.size != 0)
      listOfDates.clear()

    listOfDates.addAll(scheduleManager.getScheduleByDate(startPeriod ?: null!!, endPeriod ?: null!!,
        intent.getIntExtra(DAY_OF_WEEK_KEY, 0), week).map { it })
    repeat_value.text = when (week) {
      1 -> "(${getString(R.string.on_first_week)})"
      2 -> "(${getString(R.string.on_second_week)})"
      0 -> "(${getString(R.string.every_week)})"
      else -> "(${getString(R.string.random_dates)})"
    }

    this.week = week
  }

  private fun setColor(cat: Category, id: Int, color: Int) {
    when (id) {
      0 -> {
        exam.setColor(color)
        course_work.setColor(R.color.nb_mark_orange)
        standings.setColor(R.color.nb_mark_yellow)
        home_exam.setColor(R.color.nb_dark_cyan)
        simple_lesson.setColor(R.color.silver_white)
      }
      1 -> {
        exam.setColor(R.color.nb_mark_red)
        course_work.setColor(color)
        standings.setColor(R.color.nb_mark_yellow)
        home_exam.setColor(R.color.nb_dark_cyan)
        simple_lesson.setColor(R.color.silver_white)
      }
      2 -> {
        exam.setColor(R.color.nb_mark_red)
        course_work.setColor(R.color.nb_mark_orange)
        standings.setColor(color)
        home_exam.setColor(R.color.nb_dark_cyan)
        simple_lesson.setColor(R.color.silver_white)
      }
      3 -> {
        exam.setColor(R.color.nb_mark_red)
        course_work.setColor(R.color.nb_mark_orange)
        standings.setColor(R.color.nb_mark_yellow)
        home_exam.setColor(color)
        simple_lesson.setColor(R.color.silver_white)
      }
      4 -> {
        exam.setColor(R.color.nb_mark_red)
        course_work.setColor(R.color.nb_mark_orange)
        standings.setColor(R.color.nb_mark_yellow)
        home_exam.setColor(R.color.nb_dark_cyan)
        simple_lesson.setColor(color)
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
      R.id.add_item -> { changeSchedule(); return true }
      else -> return super.onOptionsItemSelected(item)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.add_schedule_item_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  private fun changeSchedule() {
    startPeriod?.let { start ->
      endPeriod?.let { end ->
        val begin = "${start.year}${start.monthOfYear}${start.dayOfMonth}".toLong()
        val theEnd = "${end.year}${end.monthOfYear}${end.dayOfMonth}".toLong()

        if (begin <= theEnd) {
          if (!isScheduleEdit) {
            schedule = Schedule(numberOfLesson.toString(),
                if (name_of_lesson.getText().toString().isEmpty()) "" else name_of_lesson.getText().toString(),
                start, end)
            setDataToSchedule(schedule!!)

            schedule?.dates.let {
              scheduleManager.globalList.add(schedule!!)
              setResult(Activity.RESULT_OK)
              finish()
            }
          } else {
            schedule?.numberLesson = numberOfLesson.toString()
            schedule?.nameLesson = if (name_of_lesson.getText().toString().isEmpty()) "" else name_of_lesson.getText().toString()
            schedule?.startPeriod = start
            schedule?.endPeriod = end
            setDataToSchedule(schedule!!)
            setResult(Activity.RESULT_OK)
            finish()
          }
        } else toast(getString(R.string.dates_more))
      } ?: toast(getString(R.string.set_date_of_end_period))
    } ?: toast(getString(R.string.set_date_of_begin_period))
  }

  private fun setDataToSchedule(schedule: Schedule) {
    schedule.location = Location(classroom.getText().toString(), housing.getText().toString())
    schedule.startTime = startTime
    schedule.endTime = endTime
    schedule.teacher = Teacher(name_of_teacher.getText().toString(), name_of_lesson.getText().toString())
    schedule.typeLesson = if (spinner_type_of_lesson.selectedItem.toString() == getString(R.string.practice))
      TypeLesson.SEMINAR else TypeLesson.LECTURE
    schedule.category = category
    schedule.week = week

    if (!name_of_teacher.getText().toString().isNullOrEmpty()) {
      schedule.teacher
      firebaseWrapper.pushTeacher(listOf(schedule.teacher!!)).subscribe({}, {})
    }

    if (schedule.dates.size != 0)
      schedule.dates.clear()

    schedule.dates.addAll(listOfDates.map { it })
  }

  private fun showTimeDialog() {
    val now = Calendar.getInstance()
    val dialog = TimePickerDialog.newInstance(
        this@AddScheduleActivity,
        now.get(Calendar.HOUR_OF_DAY),
        now.get(Calendar.MINUTE),
        true)
    dialog.accentColor = getColor(R.color.sch_green)
    dialog.show(fragmentManager, "")
  }

  private fun showDateDialog() {
    val now = Calendar.getInstance()
    val dialog = DatePickerDialog.newInstance(
        this@AddScheduleActivity,
        now.get(Calendar.YEAR),
        now.get(Calendar.MONTH),
        now.get(Calendar.DAY_OF_MONTH))
    dialog.accentColor = getColor(R.color.sch_green)
    dialog.show(fragmentManager, "")
  }

  override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int, second: Int) {
    val time = Time(hourOfDay, minute).toString()
    when (switcher) {
      4 -> {
        end_time.setText(time)
        endTime = Time(hourOfDay, minute)
        switcher = 0
      }
      3 -> {
        begin_time.setText(time)
        startTime = Time(hourOfDay, minute)
        switcher = 0
      }
    }
  }

  override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
    val date = Utils.getNormalizedDate(Date(dayOfMonth, monthOfYear, year))
    when (switcher) {
      2 -> {
        end_period.setText(date)
        endPeriod = Date(dayOfMonth, monthOfYear, year)
        startPeriod?.let { setListOfDates(BOTH_WEEKS) }
        switcher = 0
      }
      1 -> {
        begin_period.setText(date)
        startPeriod = Date(dayOfMonth, monthOfYear, year)
        endPeriod?.let { setListOfDates(BOTH_WEEKS) }
        switcher = 0
      }
    }
  }
}