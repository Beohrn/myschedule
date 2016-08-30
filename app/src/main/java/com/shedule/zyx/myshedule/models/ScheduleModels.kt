package com.shedule.zyx.myshedule.models

/**
 * Created by alexkowlew on 26.08.2016.
 */

class Schedule(val numberLesson: Int = 1, val nameLesson: String, val beginTime: Time, val endTime: Time) {

  var teacher: String? = ""
  var location: Location? = null
  var typeLesson: TypeLesson? = null
  var beginPeriod: Date? = null
  var endPeriod: Date? = null
}

data class Time(val hour: Int, val minute: Int)

data class Location(val classroom: String, val housing: String)

enum class TypeLesson {
  SEMINAR, LECTURE
}

data class Date(val dayOfMonth: Int, val monthOfYear: Int, val year: Int)