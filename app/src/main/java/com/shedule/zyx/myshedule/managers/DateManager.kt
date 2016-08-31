package com.shedule.zyx.myshedule.managers

import com.shedule.zyx.myshedule.models.Date
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class DateManager(var calendar: Calendar, val scheduleManager: ScheduleManager) {
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
          "${SimpleDateFormat("dd").format(calendarTemp.time)} ${getMonthName(calendarTemp.get(Calendar.MONTH))}"))
      calendarTemp.add(Calendar.DAY_OF_YEAR, 1)
    }

    calendarTemp = calendar

    for (i in 1..calendarTemp.get(Calendar.DAY_OF_WEEK)) {
      calendarTemp.add(Calendar.DAY_OF_YEAR, -1)
      dates.add(Pair(calendarTemp.get(Calendar.DAY_OF_WEEK) - 2,
          "${SimpleDateFormat("dd").format(calendarTemp.time)} ${getMonthName(calendarTemp.get(Calendar.MONTH))}"))
    }
    return dates
  }

  fun resetCalendar() = calendar.apply { Calendar.getInstance() }

  private fun getMonthName(month: Int) = DateFormatSymbols().months[month]

  fun getScheduleByDate(startDate: Date, endDate: Date, currentDayOfWeek: Int): ArrayList<Date> {
    val result = arrayListOf<Date>()
    var weeksCount = getWeeksBetween(startDate, endDate)

    val startCalendar = GregorianCalendar()
    startCalendar.set(startDate.year, startDate.monthOfYear, startDate.dayOfMonth)

    val endCalendar = GregorianCalendar()
    endCalendar.set(endDate.year, endDate.monthOfYear, endDate.dayOfMonth)
    val endDayOfWeek = endCalendar.get(Calendar.DAY_OF_WEEK)
    val startDayOfWeek = startCalendar.get(Calendar.DAY_OF_WEEK)

    var difference = 0

    if (currentDayOfWeek > startDayOfWeek) {
      difference = currentDayOfWeek - startDayOfWeek

    } else if (currentDayOfWeek < startDayOfWeek) {
      difference = currentDayOfWeek - startDayOfWeek
      startCalendar.add(Calendar.WEEK_OF_MONTH, 1)
    }

    if (endDayOfWeek == currentDayOfWeek && currentDayOfWeek == startDayOfWeek) {
      weeksCount++
    }

    for (i in 1..weeksCount) {
      val startDayOfMonth = startCalendar.get(Calendar.DAY_OF_MONTH)
      val day = startDayOfMonth + difference

      if (endDate.monthOfYear < startCalendar.get(Calendar.MONTH))
        break
      else if (endDate.monthOfYear == startCalendar.get(Calendar.MONTH))
        if (endDate.dayOfMonth < day)
          break

      result.add(Date(day, startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.YEAR)))
      startCalendar.add(Calendar.WEEK_OF_MONTH, 1)
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

}
