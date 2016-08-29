package com.shedule.zyx.myshedule.config

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shedule.zyx.myshedule.models.Schedule
import java.util.*

/**
 * Created by alexkowlew on 26.08.2016.
 */
class AppPreference(val context: Context, val gson: Gson) {

  private val TAG = AppPreference::class.java.simpleName
  private val NAME = "schedule_preference"
  private val KEY = "schedule"
  private val settings: SharedPreferences

  init { settings = PreferenceManager.getDefaultSharedPreferences(context) }

  fun saveSchedule(list: List<Schedule>) = settings.edit().putString(KEY, gson.toJson(list)).apply()

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

}