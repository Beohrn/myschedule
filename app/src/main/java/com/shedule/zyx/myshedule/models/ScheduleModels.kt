package com.shedule.zyx.myshedule.models

import java.io.Serializable

/**
 * Created by alexkowlew on 26.08.2016.
 */

class Schedule(var numberLesson: String, var nameLesson: String, var startPeriod: Date, var endPeriod: Date): Serializable {

  var teacher: Teacher? = null
  var location: Location? = null
  var typeLesson: TypeLesson? = null
  var startTime: Time? = null
  var endTime: Time? = null
  var category: Category? = null
  var dates = arrayListOf<String>()
  var week = 0
  var homework = arrayListOf<HomeWork>()
}

data class Time(val hour: Int, val minute: Int): Serializable {
  override fun toString(): String {
    if (hour < 10 && minute < 10)
      return "0$hour:0$minute"
    else if (hour < 10)
      return "0$hour:$minute"
    else if (minute < 10)
      return "$hour:0$minute"

    return "$hour:$minute"
  }
}

data class Location(val classroom: String, val housing: String): Serializable

enum class TypeLesson: Serializable { SEMINAR, LECTURE }

data class Date(val dayOfMonth: Int, val monthOfYear: Int, val year: Int): Serializable

enum class Category: Serializable { EXAM, COURSE_WORK, STANDINGS, HOME_EXAM }

data class Teacher(val nameOfTeacher: String, val nameOfLesson: String): Serializable {
  var assessmentString: String? = ""
  var averageAssessment: Double? = 0.0
}

data class HomeWork(var taskName: String, var status: Boolean) {
  var taskDescription = ""
  var deadLine = ""
}
