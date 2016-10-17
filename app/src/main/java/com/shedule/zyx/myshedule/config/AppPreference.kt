package com.shedule.zyx.myshedule.config

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.utils.Utils
import java.util.*

/**
 * Created by alexkowlew on 26.08.2016.
 */
class AppPreference(val context: Context, val gson: Gson) {

  private val OLD_KEY = "schedule"
  private val UNIVER_NAME = "univer_name"
  private val FACULTY_NAME = "facylty_name"
  private val GROUP = "group_name"
  private val ADMIN = "admin"
  private val CHANGES_COUNT = "changes_count"
  private val ADMIN_KEY = "admin_key"
  private val IS_LOGIN = "is_login_1"
  private val prefs: SharedPreferences

  init {
    prefs = PreferenceManager.getDefaultSharedPreferences(context)
  }

  fun saveSchedule(list: List<Schedule>, key: String) = prefs.edit().putString(key, gson.toJson(list)).apply()

  fun getSchedule(): ArrayList<Schedule> {
    val result: ArrayList<Schedule>
    val key: String
    if (isLogin()) key = Utils.getPrefsKeyByName(getFacultyName() ?: "", getGroupName() ?: "")
    else key = OLD_KEY

    if (prefs.contains(key)) {
      val json = prefs.getString(key, null)
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

  fun saveGroupName(group: String) = prefs.edit().putString(GROUP, group).apply()

  fun getGroupName() = prefs.getString(GROUP, null)

  fun saveAdminRights(isAdmin: Boolean) = prefs.edit().putBoolean(ADMIN, isAdmin).apply()

  fun getAdminRight() = prefs.getBoolean(ADMIN, false)

  fun saveChangesCount(count: Int) = prefs.edit().putInt(CHANGES_COUNT, count).apply()

  fun getChangesCount() = prefs.getInt(CHANGES_COUNT, 0)

  fun saveAdminKey(key: String) = prefs.edit().putString(ADMIN_KEY, key).apply()

  fun getAdminKey() = prefs.getString(ADMIN_KEY, null)

  fun saveLogin(isLogin: Boolean) = prefs.edit().putBoolean(IS_LOGIN, isLogin).apply()

  fun isLogin() = prefs.getBoolean(IS_LOGIN, false)

}