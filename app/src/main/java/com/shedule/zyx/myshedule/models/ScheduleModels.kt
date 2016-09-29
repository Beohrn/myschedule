package com.shedule.zyx.myshedule.models

import app.voter.xyz.comments.Comment
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

enum class Category: Serializable { EXAM, COURSE_WORK, STANDINGS, HOME_EXAM, SIMPLE_LESSON }

class Teacher {
  var teacherName = ""
  var lessonName = ""
  var comments = hashMapOf<String, Comment>()
  var ratings = hashMapOf<String, Double>()

  constructor(){ }

  constructor(nameOfTeacher: String, nameOfLesson: String) {
    this.teacherName = nameOfTeacher
    this.lessonName = nameOfLesson
  }
}

data class HomeWork(var taskName: String, var status: Boolean) {
  var taskDescription = ""
  var deadLine = ""
}
