package com.shedule.zyx.myshedule.managers

import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.models.Schedule
import java.util.*

/**
 * Created by alexkowlew on 29.08.2016.
 */
class ScheduleManager(val globalList: ArrayList<Schedule>, val prefs: AppPreference) {

  fun saveSchedule() {
    prefs.saveSchedule(globalList)
  }

}

