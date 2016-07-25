package com.shedule.zyx.myshedule.managers

import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class DateManager(var calendar: Calendar) {
  var dates = arrayListOf<Pair<Int, String>>()

  fun getPositionByDate(year: Int, month: Int, day: Int): Int {
    calendar.set(year, month, day)
    return getDayOfWeek()
  }

  fun getPositionByDate(): Int {
    return getDayOfWeek()
  }

  fun getMonthName(month: Int) = DateFormatSymbols().months[month]

  fun getChoiceDay() = calendar.get(Calendar.DAY_OF_MONTH)

  fun updateCalendar(year: Int, month: Int, day: Int) {
    dates.clear()
    calendar.set(year, month, day)
    val calendarTemp = calendar

    for (i in calendarTemp.get(Calendar.DAY_OF_WEEK)..6) {
      dates.add(Pair(calendarTemp.get(Calendar.DAY_OF_WEEK) - 2,
          "${SimpleDateFormat("dd").format(calendarTemp.time)} ${getMonthName(calendarTemp.get(Calendar.MONTH))}"))
      calendarTemp.add(Calendar.DAY_OF_YEAR, 1)
    }

    calendarTemp.set(year, month, day)

    for (i in 1..calendarTemp.get(Calendar.DAY_OF_WEEK)) {
      calendarTemp.add(Calendar.DAY_OF_YEAR, -1)
      dates.add(Pair(calendarTemp.get(Calendar.DAY_OF_WEEK) - 2,
          "${SimpleDateFormat("dd").format(calendarTemp.time)} ${getMonthName(calendarTemp.get(Calendar.MONTH))}"))
    }
  }

  fun updateCalendar() {
    calendar = Calendar.getInstance()
    updateCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
  }

  fun getDayFromPosition(position: Int): String {
    dates.forEach {
      if (it.first == position) return "${it.second}"
    }
    return ""
  }

  private fun getDayOfWeek(): Int {
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
      1 -> 0
      7 -> 0
      else -> (calendar.get(Calendar.DAY_OF_WEEK) - 2)
    }
  }
}
