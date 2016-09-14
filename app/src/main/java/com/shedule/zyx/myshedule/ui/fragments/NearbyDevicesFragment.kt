package com.shedule.zyx.myshedule.ui.fragments

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.managers.BluetoothManager
import kotlinx.android.synthetic.main.fragment_list_of_devices.*
import org.jetbrains.anko.onItemClick
import javax.inject.Inject

/**
 * Created by Alexander on 20.08.2016.
 */
class NearbyDevicesFragment : Fragment() {

  @Inject
  lateinit var bluetoothManager: BluetoothManager

  lateinit var adapter: ArrayAdapter<String>
  val nearbyDevicesList = arrayListOf<String>()
  var devices = arrayListOf<Pair<String, String>>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ScheduleApplication.getComponent().inject(this)

    val intentFilter = IntentFilter()
    with(intentFilter) {
      addAction(bluetoothManager.FOUND)
      addAction(bluetoothManager.DISCOVERY_STARTED)
      addAction(bluetoothManager.DISCOVERY_FINISHED)
    }

    context.registerReceiver(receiver, intentFilter)
    bluetoothManager.startDiscovery()
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
      inflater!!.inflate(R.layout.fragment_nearby_devices, container, false)

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nearbyDevicesList)
    list_of_devices.adapter = adapter

    list_of_devices.onItemClick { adapterView, view, i, l ->
      bluetoothManager.connect(devices[i].first)
      nearbyDevicesList.removeAt(i)
      adapter.notifyDataSetChanged()
    }
  }

  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      intent?.let {
        if (bluetoothManager.DISCOVERY_STARTED.equals(it.action))
        else if (bluetoothManager.DISCOVERY_FINISHED.equals(it.action))
          context?.unregisterReceiver(this)
        else if (bluetoothManager.FOUND.equals(it.action)) {
          val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
          devices.add(Pair(device.address, device.name))
          if (devices.size != 0) {
            nearbyDevicesList.addAll(devices.map { it.second }.filter { !nearbyDevicesList.contains(it) }
                .distinctBy { bluetoothManager.getPairedDevices().map { it.second } })
            adapter.notifyDataSetChanged()
          }
        }
      }

    }
  }

}