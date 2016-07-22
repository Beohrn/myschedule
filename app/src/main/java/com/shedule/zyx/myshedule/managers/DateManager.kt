package com.shedule.zyx.myshedule.managers

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

class DateManager(val localDate: LocalDate, val localTime: LocalTime, val dateTime: DateTime) {

  fun getCurrentDate(): String = "${dateTime.dayOfWeek().asText} " +
      "${dateTime.dayOfMonth}-e " +
      "${dateTime.monthOfYear().asText} " +
      "${dateTime.year} года"

  fun getCurrentYear() = localDate.year

  fun getCurrentMonthOfYear() = localDate.monthOfYear

  fun getCurrentMonthNameOfYear() = dateTime.monthOfYear().asText

  fun getCurrentDayOfMonth() = localDate.dayOfMonth

  fun getCurrentDayOfWeek() = dateTime.dayOfWeek().asText

  fun getCurrentTime() = localTime

  fun getCurrentHourOfDay() = localTime.hourOfDay

  fun getCurrentMinutes() = localTime.minuteOfHour
}
