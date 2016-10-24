package com.shedule.zyx.myshedule.models

import app.voter.xyz.comments.Comment
import java.io.Serializable

/**
 * Created by alexkowlew on 26.08.2016.
 */

class Schedule : Serializable {

  var numberLesson = ""
  var nameLesson = ""
  var startPeriod: Date? = null
  var endPeriod: Date? = null
  var teacher: Teacher? = null
  var location: Location? = null
  var typeLesson = ""
  var startTime: Time? = null
  var endTime: Time? = null
  var category = ""
  var dates = arrayListOf<String>()
  var week = 0
  var homework = arrayListOf<HomeWork>()

  constructor() {
  }

  constructor(numberLesson: String, nameLesson: String, startPeriod: Date?, endPeriod: Date?) {
    this.numberLesson = numberLesson
    this.nameLesson = nameLesson
    this.startPeriod = startPeriod
    this.endPeriod = endPeriod
  }
}

class Time : Serializable {
  var hour = 0
  var minute = 0

  constructor() {
  }

  constructor(hour: Int, minute: Int) {
    this.hour = hour
    this.minute = minute
  }


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

class Location : Serializable {
  var classroom = ""
  var housing = ""

  constructor() {
  }

  constructor(classroom: String, housing: String) {
    this.classroom = classroom
    this.housing = housing
  }

}

class Date : Serializable {
  var dayOfMonth = 0
  var monthOfYear = 0
  var year = 0

  constructor() {
  }

  constructor(dayOfMonth: Int, monthOfYear: Int, year: Int) {
    this.dayOfMonth = dayOfMonth
    this.monthOfYear = monthOfYear
    this.year = year
  }
}

class Teacher {
  var teacherName = ""
  var lessonName = ""
  var comments = hashMapOf<String, Comment>()
  var ratings = hashMapOf<String, Double>()

  constructor() {
  }

  constructor(nameOfTeacher: String, nameOfLesson: String) {
    this.teacherName = nameOfTeacher
    this.lessonName = nameOfLesson
  }
}

class HomeWork {
  var taskDescription = ""
  var taskName = ""
  var status = false
  var deadLine = ""

  constructor() {
  }

  constructor(taskName: String, status: Boolean) {
    this.taskName = taskName
    this.status = status
  }
}

class Group {
  var groupName = ""
  var countChange = 0
  var admins = arrayListOf<String>()
  var schedule = arrayListOf<Schedule>()

  constructor() {
  }

  constructor(groupName: String) {
    this.groupName = groupName
  }
}
