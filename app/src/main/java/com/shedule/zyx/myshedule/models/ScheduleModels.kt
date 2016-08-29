package com.shedule.zyx.myshedule.models

/**
 * Created by alexkowlew on 26.08.2016.
 */

class Schedule(val number: String, val name: String, val time: Time, val date: Date) {

  var teacher: String? = ""
  var location: Location? = null
  var typeLesson: TypeLesson? = null
  var periodOfLesson: String? = ""
}

data class Time(val begin: String, val end: String) {
  override fun toString() = "$begin - $end"
}

data class Location(val classroom: String, val housing: String) {
  override fun toString() = "$classroom - $housing"
}

data class Teacher(val name: String)

enum class TypeLesson {
  SEMINAR, LECTURE
}

data class Date(val dayOfMonth: Int, val monthOfYear: Int, val year: Int)