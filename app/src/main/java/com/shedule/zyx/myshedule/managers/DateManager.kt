package com.shedule.zyx.myshedule.managers

import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class DateManager(var calendar: Calendar) {
  var dates = arrayListOf<Pair<Int, String>>()

  fun getPositionByCalendar(year: Int, month: Int, day: Int): Int {
    calendar.set(year, month, day)
    return getPositionByCalendar()
  }

  fun getPositionByCalendar(): Int {
    if (calendar.get(Calendar.DAY_OF_WEEK) != 1 && calendar.get(Calendar.DAY_OF_WEEK) != 7) {
      return calendar.get(Calendar.DAY_OF_WEEK) - 2
    } else return 0
  }

  fun getDayByPosition(position: Int): String {
    if (dates.isNotEmpty()) {
      dates.forEach { if (it.first == position) return "${it.second}" }
    } else {
      updateCalendar().forEach {
        if (it.first == position) return "${it.second}"
      }
    }
    return ""
  }

  fun updateCalendar(year: Int, month: Int, day: Int) {
    calendar.set(year, month, day)
    updateCalendar()
  }

  private fun updateCalendar(): List<Pair<Int, String>> {
    dates.clear()
    var calendarTemp = calendar

    for (i in calendarTemp.get(Calendar.DAY_OF_WEEK)..6) {
      dates.add(Pair(calendarTemp.get(Calendar.DAY_OF_WEEK) - 2,
          "${SimpleDateFormat("d").format(calendarTemp.time)}-${calendarTemp.get(Calendar.MONTH)}-" +
              "${calendarTemp.get(Calendar.YEAR)}"))
      calendarTemp.add(Calendar.DAY_OF_YEAR, 1)
    }

    calendarTemp = calendar

    for (i in 1..calendarTemp.get(Calendar.DAY_OF_WEEK)) {
      calendarTemp.add(Calendar.DAY_OF_YEAR, -1)
      dates.add(Pair(calendarTemp.get(Calendar.DAY_OF_WEEK) - 2,
          "${SimpleDateFormat("d").format(calendarTemp.time)}-${calendarTemp.get(Calendar.MONTH)}-" +
              "${calendarTemp.get(Calendar.YEAR)}"))
    }
    return dates
  }

  fun getMonthName(month: Int) = DateFormatSymbols().months[month]
}
