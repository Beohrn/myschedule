package com.shedule.zyx.myshedule.models

import java.util.*

/**
 * Created by alexkowlew on 26.08.2016.
 */

class Schedule(val numberLesson: String, val nameLesson: String, val startPeriod: Date, val endPeriod: Date) {


  var teacher: String? = ""
  var location: Location? = null
  var typeLesson: TypeLesson? = null
  var startTime: Time? = null
  var endTime: Time? = null
  var category: Category? = null
  var dates = ArrayList<Date>()
}

data class Time(val hour: Int, val minute: Int)

data class Location(val classroom: String, val housing: String)

enum class TypeLesson { SEMINAR, LECTURE }

data class Date(val dayOfMonth: Int, val monthOfYear: Int, val year: Int)

enum class Category { EXAM, COURSE_WORK, STANDINGS, HOME_EXAM }
