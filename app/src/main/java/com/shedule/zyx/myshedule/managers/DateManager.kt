package com.shedule.zyx.myshedule.managers

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

class DateManager(localDate : LocalDate, localTime : LocalTime,
                  dateTime : DateTime) {

    fun getCurrentDate() : String = "${DateTime().dayOfWeek().asText} " +
            "${DateTime().dayOfMonth}-e " +
            "${DateTime().monthOfYear().asText} " +
            "${DateTime().year} года"

    fun getCurrentYear() : Int = LocalDate.now().year

    fun getCurrentMonthOfYear() : Int = LocalDate.now().monthOfYear

    fun getCurrentMonthNameOfYear() : String = DateTime().monthOfYear().asText

    fun getCurrentDayOfMonth() : Int = LocalDate.now().dayOfMonth

    fun getCurrentDayOfWeek() : String = DateTime().dayOfWeek().asText

    fun getCurrentTime() : LocalTime = LocalTime.now()

    fun getCurrentHourOfDay() : Int = LocalTime.now().hourOfDay

    fun getCurrentMinutes() : Int = LocalTime.now().minuteOfHour

}
