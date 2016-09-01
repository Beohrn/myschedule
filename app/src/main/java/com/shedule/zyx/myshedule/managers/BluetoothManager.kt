package com.shedule.zyx.myshedule.managers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Build
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.utils.Utils
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by Alexander on 03.08.2016.
 */
class BluetoothManager(val context: Context, val bt: BluetoothSPP):
    BluetoothSPP.AutoConnectionListener, BluetoothSPP.BluetoothConnectionListener {


  private val TAG = BluetoothManager::class.java.simpleName
  val btAdapter: BluetoothAdapter

  val FOUND = BluetoothDevice.ACTION_FOUND
  val DISCOVERY_STARTED = BluetoothAdapter.ACTION_DISCOVERY_STARTED
  val DISCOVERY_FINISHED = BluetoothAdapter.ACTION_DISCOVERY_FINISHED

  //todo don't forget the set this parameter before sending
  var schedule = ArrayList<Schedule>()

  init {
    btAdapter = BluetoothAdapter.getDefaultAdapter()
  }

  fun getPairedDevices() = btAdapter.bondedDevices.map { Pair(it.address, it.name.toString()) }

  fun connect(address: String) = bt.connect(address)

  private fun send(message: String) = bt.send(message, true)

  fun setConnectionListener(listener: BluetoothSPP.BluetoothConnectionListener) = bt.setBluetoothConnectionListener(listener)

  fun setReceiveListener(listener: ReceiveManager) = bt.setOnDataReceivedListener(listener)

  fun serviceAvailable() = if (bt.isServiceAvailable) true else false

  fun setupService() {
    bt.setupService()
    bt.startService(BluetoothState.DEVICE_ANDROID)
    setConnectionListener(this)

    if (btAdapter.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
      makeDiscoverable()
  }

  fun stopService() {
    if (bt.isServiceAvailable)
      bt.stopService()
  }

  fun bluetoothEnabled() = if (btAdapter.isEnabled) true else false

  fun autoConnect() = bt.enable()

  override fun onAutoConnectionStarted() = context.toast("Auto Connect")

  override fun onNewConnection(name: String?, address: String?) = context.toast("$name | $address")

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

  override fun onDeviceDisconnected() { }

  override fun onDeviceConnected(name: String?, address: String?) {
    send(Utils.toJson(schedule))
  }

  override fun onDeviceConnectionFailed() { }
}


