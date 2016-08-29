package com.shedule.zyx.myshedule.managers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.models.Schedule
import java.util.*
import javax.inject.Inject

/**
 * Created by alexkowlew on 26.08.2016.
 */
class PreferencesManager(context: Context) {

  private val TAG = PreferencesManager::class.java.simpleName
  private val NAME = "schedule_preference"
  private val KEY = "schedule"
  private val settings: SharedPreferences
  private val editor: SharedPreferences.Editor

  @Inject
  lateinit var gson: Gson

  init {
    settings = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    editor = settings.edit()
    ScheduleApplication.getComponent().inject(this)
  }

  fun saveSchedule(list: List<Schedule>) = editor.putString(KEY, gson.toJson(list)).apply()

  fun getSchedule(): ArrayList<Schedule> {
    val result: ArrayList<Schedule>
    if (settings.contains(KEY)) {
      val json = settings.getString(KEY, null)
      Log.i(TAG, "$json")
      val type = object : TypeToken<ArrayList<Schedule>>() {}.type
      result = gson.fromJson(json, type)
    } else {
      result = ArrayList<Schedule>()
      return result
    }

    return result
  }

  fun addItem(schedule: Schedule) {
    val schedules = getSchedule()
    schedules.add(schedule)
    saveSchedule(schedules)

  }

  fun removeItem(index: Int) {
    val res = getSchedule()
    res.removeAt(index)
    saveSchedule(res)
  }

}