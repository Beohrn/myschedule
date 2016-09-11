package com.shedule.zyx.myshedule.managers

import android.content.Context
import android.util.Log
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shedule.zyx.myshedule.models.Schedule
import java.util.*

class ReceiveManager(val context: Context,
                     val scheduleManager: ScheduleManager,
                     val gson: Gson) : BluetoothSPP.OnDataReceivedListener {

  private val TAG = ReceiveManager::class.java.simpleName

  override fun onDataReceived(data: ByteArray?, message: String?) {
    Log.d(TAG, message)
    val result: ArrayList<Schedule>
    val type = object : TypeToken<ArrayList<Schedule>>() {}.type
    result = gson.fromJson(message ?: "", type)
    scheduleManager.globalList.addAll(result.map { it })
  }

}