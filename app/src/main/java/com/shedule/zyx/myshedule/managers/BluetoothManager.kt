package com.shedule.zyx.myshedule.managers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.shedule.zyx.myshedule.interfaces.FoundDeviceReceiverCallback
import com.shedule.zyx.myshedule.receivers.FoundDeviceReceiver
import java.util.*

/**
 * Created by Alexander on 03.08.2016.
 */
class BluetoothManager constructor(var bt: BluetoothSPP, var context: Context) : FoundDeviceReceiverCallback {

  private val TAG = BluetoothManager::class.java.simpleName
  val btAdapter: BluetoothAdapter

  val ACTION_ENABLE = BluetoothAdapter.ACTION_REQUEST_ENABLE
  val REQUEST_ENABLE = BluetoothState.REQUEST_ENABLE_BT

  val deviceReceiver = FoundDeviceReceiver(this)

  var listOfNearbyDevices = ArrayList<BluetoothDevice>()

  init {
    btAdapter = BluetoothAdapter.getDefaultAdapter()
  }

  private fun bondedDevices(): ArrayList<BluetoothDevice> {
    val result = arrayListOf<BluetoothDevice>()
    result.addAll(btAdapter.bondedDevices.map { it })
    return result
  }

  fun getDevices(): ArrayList<String> {
    val pairedDevices = ArrayList<String>()
    bondedDevices().forEach {
      pairedDevices.add(it.name)
    }
    return pairedDevices
  }

  fun connect(index: Int, belong: Boolean) {
    var address: String
    if (belong)
      address = bondedDevices()[index].address
    else
      address = listOfNearbyDevices[index].address

    if (serviceAvailable()) {
      bt.connect(address)
    }
  }

  fun send(message: String) {
    bt.send(message, true)
  }

  fun setConnectionListener(listener: BTConnectionManager) {
    bt.setBluetoothConnectionListener(listener)
  }

  fun setReceiveListener(listener: ReceiveManager) {
    bt.setOnDataReceivedListener(listener)
  }

  fun serviceAvailable() = if (bt.isServiceAvailable) true else false

  fun setupService() {
    bt.setupService()
    bt.startService(BluetoothState.DEVICE_ANDROID)

    if (btAdapter.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
      makeDiscoverable()

    Log.i(TAG, "${btAdapter.scanMode}")
  }

  fun stopService() {
    if (bt.isServiceAvailable)
      bt.stopService()
  }

  fun bluetoothEnabled(): Boolean {
    if (btAdapter.isEnabled)
      return true
    else
      return false
  }

  fun getNearbyDevices(): ArrayList<BluetoothDevice> {
    val list = ArrayList<BluetoothDevice>()
    listOfNearbyDevices.forEach {
      list.add(it)
    }
    return list
  }


  override fun onDeviceFound(device: BluetoothDevice) {
    listOfNearbyDevices.add(device)
  }

  private fun nearbyDevices() {
    deviceReceiver.register(context)
    btAdapter.startDiscovery()
//        val filter = IntentFilter()
//        filter.addAction(BluetoothDevice.ACTION_FOUND)
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
//
//        context.registerReceiver(broadcastReceiver, filter)
//
//        btAdapter.startDiscovery()
  }

//    private val broadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            val action = intent!!.action
//            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                context!!.unregisterReceiver(this)
//            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                listOfNearbyDevices.add(device)
//
//            }
//        }
//    }

  private fun makeDiscoverable() {
    val baClass = BluetoothAdapter::class.java
    val methods = baClass.declaredMethods
    val method = methods[54]
    method.invoke(btAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 300)

  }
}


