package com.shedule.zyx.myshedule.ui.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.ScheduleApplication.getComponent
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.managers.BluetoothManager
import com.shedule.zyx.myshedule.managers.DateManager
import com.shedule.zyx.myshedule.managers.ScheduleManager
import org.jetbrains.anko.indeterminateProgressDialog
import rx.Subscription
import javax.inject.Inject

/**
 * Created by alexkowlew on 12.10.2016.
 */
abstract class BaseActivity : AppCompatActivity() {

  @Inject
  lateinit var dateManager: DateManager

  @Inject
  lateinit var bluetoothManager: BluetoothManager

  @Inject
  lateinit var scheduleManager: ScheduleManager

  @Inject
  lateinit var prefs: AppPreference

  @Inject
  lateinit var gson: Gson

  @Inject
  lateinit var firebaseWrapper: FirebaseWrapper

  @Inject
  lateinit var auth: FirebaseAuth

  @Inject
  lateinit var firebaseRef: DatabaseReference

  @Inject
  lateinit var deviceToken: String

  lateinit var dialog: ProgressDialog

  var subscription: Subscription? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getComponent().inject(this)
  }

  fun saveSchedule() {
    if (!prefs.getFacultyName().isNullOrEmpty() && !prefs.getGroupName().isNullOrEmpty())
      scheduleManager.saveSchedule()
  }

  fun getSchedule() { scheduleManager.globalList = prefs.getSchedule() }

  fun showProgressDialog(message: String) {
    dialog = indeterminateProgressDialog(message)
  }

  fun hideProgressDialog() {
    dialog.hide()
  }

  override fun onPause() {
    subscription?.unsubscribe()
    super.onPause()
  }

}