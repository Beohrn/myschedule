package com.shedule.zyx.myshedule.managers

import app.voter.xyz.comments.Comment
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.models.Date
import com.shedule.zyx.myshedule.models.HomeWork
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.utils.Utils.Companion.getKeyByName
import java.util.*

/**
 * Created by alexkowlew on 29.08.2016.
 */
class ScheduleManager(val globalList: ArrayList<Schedule>, val prefs: AppPreference) {

  fun saveSchedule() {
    prefs.saveSchedule(globalList)
  }

  fun getScheduleByDay(day: String) = globalList.map { schedule ->
    schedule.dates.filter { it.equals(day) }.map { schedule }
  }.flatMap { it -> it.map { it } }.sortedBy { it.numberLesson.toInt() }

  fun getScheduleByDate(startDate: Date, endDate: Date, currentDayOfWeek: Int, week: Int): ArrayList<String> {
    val result = arrayListOf<String>()
    var weeksCount = getWeeksBetween(startDate, endDate)

    val startCalendar = GregorianCalendar()
    startCalendar.set(startDate.year, startDate.monthOfYear, startDate.dayOfMonth)

    val endCalendar = GregorianCalendar()
    endCalendar.set(endDate.year, endDate.monthOfYear, endDate.dayOfMonth)
    val endDayOfWeek = endCalendar.get(Calendar.DAY_OF_WEEK)
    val startDayOfWeek = startCalendar.get(Calendar.DAY_OF_WEEK)

    var difference = 0
    var range = 1

    if (currentDayOfWeek >= startDayOfWeek) {
      difference = currentDayOfWeek - startDayOfWeek

      when (week) {
        1 -> {
          range++; weeksCount /= 2
        }
        2 -> {
          startCalendar.add(Calendar.WEEK_OF_MONTH, 1)
          range++
          weeksCount /= 2
        }
      }
    } else if (currentDayOfWeek < startDayOfWeek) {
      difference = currentDayOfWeek - startDayOfWeek

      when (week) {
        1 -> {
          startCalendar.add(Calendar.WEEK_OF_MONTH, 2)
          range++
          weeksCount /= 2
        }
        2 -> {
          startCalendar.add(Calendar.WEEK_OF_MONTH, 1)
          range++
          weeksCount /= 2
        }
        else -> startCalendar.add(Calendar.WEEK_OF_MONTH, 1)
      }
    }

    if (endDayOfWeek == currentDayOfWeek && currentDayOfWeek == startDayOfWeek) {
      weeksCount++
    }
    startCalendar.add(Calendar.DAY_OF_MONTH, difference)
    for (i in 1..weeksCount) {
      val day = startCalendar.get(Calendar.DAY_OF_MONTH)

      if (endDate.monthOfYear < startCalendar.get(Calendar.MONTH) || endDate.year < startCalendar.get(Calendar.YEAR))
        break
      else if (endDate.monthOfYear == startCalendar.get(Calendar.MONTH))
        if (endDate.dayOfMonth < day)
          break

      result.add("$day-${startCalendar.get(Calendar.MONTH)}-${startCalendar.get(Calendar.YEAR)}")
      startCalendar.add(Calendar.WEEK_OF_MONTH, range)
    }
    return result
  }

  private fun getWeeksBetween(startDate: Date, endDate: Date): Int {
    var weeks = 0
    val start = resetCalendar(startDate).time
    val end = resetCalendar(endDate).time

    val cal = GregorianCalendar()
    cal.time = start

    while (cal.time.before(end)) {
      cal.add(Calendar.WEEK_OF_YEAR, 1)
      weeks++
    }

    return weeks
  }

  private fun resetCalendar(date: Date): Calendar {
    val calendar = GregorianCalendar()
    calendar.set(date.year, date.monthOfYear, date.dayOfMonth)
    return calendar
  }

  fun getTeachers() = globalList.map { it.teacher }.filterNotNull().map { it }.distinctBy { it.teacherName }

  fun removeSchedule(schedule: Schedule) {
    globalList.remove(schedule)
  }

  fun getAllHomework() = globalList.map { it }.filter { it.homework.size != 0 }

  fun getHomeWork(schedule: Schedule) = schedule.homework

  fun getHomeWorkByDate(schedule: Schedule, date: String) = schedule.homework.map { it }.filter { it.deadLine.equals(date)  }

  var editSchedule: Schedule? = null

  fun setCommentToTeacher(keyToComment: String, comment: Comment, teacherName: String) {
    val teacher = getTeachers().filter { getKeyByName(it.teacherName).equals(teacherName) }
        .firstOrNull()

    teacher?.let { it.comments.put(keyToComment, comment) }
  }

  var editHomework: HomeWork? = null
}

