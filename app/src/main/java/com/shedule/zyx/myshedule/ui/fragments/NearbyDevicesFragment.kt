package com.shedule.zyx.myshedule.ui.fragments

import android.app.ProgressDialog
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.fragment_list_of_devices.*
import org.jetbrains.anko.onItemClick
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.selector

/**
 * Created by Alexander on 20.08.2016.
 */
class NearbyDevicesFragment: BaseFragment() {
  override var contentView = R.layout.fragment_nearby_devices
  private var progressDialog: ProgressDialog? = null

  lateinit var adapter: ArrayAdapter<String>
  val nearbyDevicesList = arrayListOf<String>()
  var devices = arrayListOf<Pair<String, String>>()

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val intentFilter = IntentFilter()
    with(intentFilter) {
      addAction(bluetoothManager.FOUND)
      addAction(bluetoothManager.DISCOVERY_STARTED)
      addAction(bluetoothManager.DISCOVERY_FINISHED)
    }

    context.registerReceiver(receiver, intentFilter)
    bluetoothManager.startDiscovery()

    adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nearbyDevicesList)
    list_of_devices.adapter = adapter

    list_of_devices.onItemClick { adapterView, view, i, l ->
      progressDialog = indeterminateProgressDialog(getString(R.string.connecting))
      bluetoothManager.connect(devices[i].first)
      bluetoothManager.setStateListener(BluetoothSPP.BluetoothStateListener { state ->
        if (state == bluetoothManager.STATE_CONNECTED) {
          progressDialog?.hide()
          if (isAdded)
            selector("", listOf(getString(R.string.send), getString(R.string.cancel))) {
              when (it) {
                0 -> { bluetoothManager.send(); bluetoothManager.disconnect() }
                1 -> bluetoothManager.disconnect()
              }
            }
        } else if (state == bluetoothManager.STATE_CONNECTING) progressDialog?.hide()
      })

      nearbyDevicesList.removeAt(i)
      adapter.notifyDataSetChanged()
    }
  }

  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      intent?.let {
        if (bluetoothManager.DISCOVERY_STARTED == it.action)
        else if (bluetoothManager.DISCOVERY_FINISHED == it.action)
          context?.unregisterReceiver(this)
        else if (bluetoothManager.FOUND.equals(it.action)) {
          val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
          devices.add(Pair(device.address, device.name))
          if (devices.size != 0) {
            nearbyDevicesList.addAll(devices.map { it.second }
                .filter { !nearbyDevicesList.contains(it) }
                .filterNotNull()
                .distinctBy { bluetoothManager.getPairedDevices().map { it.second } })
            adapter.notifyDataSetChanged()
          }
        }
      }

    }
  }
}