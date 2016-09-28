package com.shedule.zyx.myshedule.config

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shedule.zyx.myshedule.models.Schedule
import java.util.*

/**
 * Created by alexkowlew on 26.08.2016.
 */
class AppPreference(val context: Context, val gson: Gson) {

  private val KEY = "schedule"
  private val UNIVER_NAME = "univer_name"
  private val FACULTY_NAME = "facylty_name"
  private val IS_FIRST_TIME_LAUNCH = "is_first_time_launch"
  private val prefs: SharedPreferences

  init {
    prefs = PreferenceManager.getDefaultSharedPreferences(context)
  }

  fun saveSchedule(list: List<Schedule>) = prefs.edit().putString(KEY, gson.toJson(list)).apply()

  fun getSchedule(): ArrayList<Schedule> {
    val result: ArrayList<Schedule>
    if (prefs.contains(KEY)) {
      val json = prefs.getString(KEY, null)
      val type = object : TypeToken<ArrayList<Schedule>>() {}.type
      result = gson.fromJson(json, type)
    } else {
      result = ArrayList<Schedule>()
      return result
    }
    return result
  }

  fun saveUniverName(name: String) = prefs.edit().putString(UNIVER_NAME, name).apply()

  fun getUniverName() = prefs.getString(UNIVER_NAME, null)

  fun saveFacultyName(name: String) = prefs.edit().putString(FACULTY_NAME, name).apply()

  fun getFacultyName() = prefs.getString(FACULTY_NAME, null)
}