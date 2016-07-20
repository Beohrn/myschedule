package app.voter.xyz.config

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class AppPreferences(context: Context) {
  private val prefs: SharedPreferences

  init {
    prefs = PreferenceManager.getDefaultSharedPreferences(context)
  }
}
