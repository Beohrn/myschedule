package com.shedule.zyx.myshedule.managers

import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class DateManager(val calendar: Calendar) {
  var dates = arrayListOf<Pair<String, String>>()

  fun getDayOfWeek(year: Int, month: Int, day: Int): Int {
    calendar.set(year, month, day)
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
      1 -> 0
      7 -> 0
      else -> (calendar.get(Calendar.DAY_OF_WEEK) - 2)
    }
  }

  fun getDayOfWeek(): Int {
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
      1 -> 0
      7 -> 0
      else -> (calendar.get(Calendar.DAY_OF_WEEK) - 2)
    }
  }

  fun getMonthName(month: Int) = DateFormatSymbols().months[month]

  fun getChoiceDay() = calendar.get(Calendar.DAY_OF_MONTH)

  fun updateCalendar(year: Int, month: Int, day: Int) {
    dates.clear()
    calendar.set(year, month, day)
    val calendarTemp = calendar
    calendarTemp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    for (i in 0..6) {
      dates.add(Pair(SimpleDateFormat("dd").format(calendarTemp.time), getMonthName(month)))
      calendarTemp.add(Calendar.DATE, 1)
    }
  }

  fun updateCalendar() {
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    dates.clear()
    for (i in 0..6) {
      dates.add(Pair(SimpleDateFormat("dd").format(calendar.time), getMonthName(calendar.get(Calendar.MONTH))))
      calendar.add(Calendar.DATE, 1)
    }
  }

  fun getDayFromPosition(position: Int) = "${dates[position].first} ${dates[position].second}"
}
