package com.shedule.zyx.myshedule.managers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED
import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED
import android.bluetooth.BluetoothDevice.ACTION_FOUND
import android.content.Context
import android.content.Intent
import android.os.Build
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.utils.Utils
import java.util.*

/**
 * Created by Alexander on 03.08.2016.
 */
class BluetoothManager(val context: Context, val bt: BluetoothSPP, val gson: Gson): BluetoothConnectionListener,
    OnDataReceivedListener {

  val btAdapter: BluetoothAdapter
  var onScheduleReceiveListener: OnScheduleReceiveListener? = null

  val FOUND = ACTION_FOUND
  val DISCOVERY_STARTED = ACTION_DISCOVERY_STARTED
  val DISCOVERY_FINISHED = ACTION_DISCOVERY_FINISHED
  val STATE_CONNECTED = BluetoothState.STATE_CONNECTED
  val STATE_CONNECTING = BluetoothState.STATE_CONNECTING

  //todo don't forget the set this parameter before sending
  var schedule = ArrayList<Schedule>()

  init {
    btAdapter = BluetoothAdapter.getDefaultAdapter()
  }

  fun setScheduleReceiveListener(listener: OnScheduleReceiveListener) {
    this.onScheduleReceiveListener = listener
  }

  fun getPairedDevices() = btAdapter.bondedDevices.map { Pair(it.address, it.name.toString()) }

  fun connect(address: String) = bt.connect(address)

  fun disconnect() = bt.disconnect()

  fun send() = bt.send(Utils.toJson(schedule), true)

  fun setConnectionListener(listener: BluetoothConnectionListener) = bt.setBluetoothConnectionListener(listener)

  fun serviceAvailable() = if (bt.isServiceAvailable) true else false

  fun setupService() {
    bt.setupService()
    bt.startService(BluetoothState.DEVICE_ANDROID)
    setConnectionListener(this)
    bt.setOnDataReceivedListener(this)

    if (btAdapter.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
      makeDiscoverable()
  }

  fun stopService() { if (bt.isServiceAvailable) bt.stopService() }

  fun bluetoothEnabled() = btAdapter.isEnabled

  fun autoConnect() = bt.enable()

  fun startDiscovery() = btAdapter.startDiscovery()

  private fun makeDiscoverable() {
    if (Build.VERSION.SDK_INT >= 21) {
      val baClass = BluetoothAdapter::class.java
      val methods = baClass.declaredMethods
      val method = methods[54]
      method.invoke(btAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 300)
    } else {
      val intentFilter = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
      intentFilter.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
      intentFilter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intentFilter)
    }
  }

  fun setStateListener(stateListener: BluetoothSPP.BluetoothStateListener) =
    bt.setBluetoothStateListener(stateListener)

  override fun onDeviceDisconnected() { }
  override fun onDeviceConnected(name: String?, address: String?) { }
  override fun onDeviceConnectionFailed() { }

  override fun onDataReceived(data: ByteArray?, message: String?) {
    val type = object : TypeToken<ArrayList<Schedule>>() {}.type
    val schedule: ArrayList<Schedule> = gson.fromJson(message ?: "", type)
    onScheduleReceiveListener?.let {
      it.onScheduleReceived(schedule)
    }
  }

  interface OnScheduleReceiveListener { fun onScheduleReceived(schedules: ArrayList<Schedule>) }
}


