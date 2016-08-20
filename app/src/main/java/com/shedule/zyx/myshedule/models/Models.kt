package com.shedule.zyx.myshedule.models

import java.util.*

/**
 * Created by bogdan on 02.08.16.
 */

data class Schedule(val lessonNumber: String, val teacher: Teacher,
                    val date: Dates, val place: Place, val homeWork: HomeWork)

data class HomeWork(val homeTasks: List<String>)

data class Place(val place: String, val type: String)

data class Dates(val dates: List<Date>)

data class Teacher(val firstName: String, val lastName: String, val middleName: String)


