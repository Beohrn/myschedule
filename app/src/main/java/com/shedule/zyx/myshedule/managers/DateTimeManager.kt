package com.shedule.zyx.myshedule.managers

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime


class DateTimeManager private constructor() {

    private object Holder {
        val INSTANCE = DateTimeManager()
    }

    companion object {
        val instance : DateTimeManager by lazy { Holder.INSTANCE }
    }

    fun getCurrentDateInNumbers() : LocalDate = LocalDate.now()

    fun getCurrentDateInString() : String = "${DateTime().dayOfWeek().asText} " +
            "${DateTime().dayOfMonth}-e " +
            "${DateTime().monthOfYear().asText} " +
            "${DateTime().year} года"



    fun getCurrentYear() : Int = LocalDate.now().year

    fun getCurrentMonthOfYearInNumber() : Int = LocalDate.now().monthOfYear

    fun getCurrentMontOfYearInString() : String = DateTime().monthOfYear().asText

    fun getCurrentDayOfMonth() : Int = LocalDate.now().dayOfMonth

    fun getCurrentDayOfWeekInString() : String = DateTime().dayOfWeek().asText

    fun getCurrentTime() : LocalTime = LocalTime.now()

    fun getCurrentHourOfDay() : Int = LocalTime.now().hourOfDay


}
