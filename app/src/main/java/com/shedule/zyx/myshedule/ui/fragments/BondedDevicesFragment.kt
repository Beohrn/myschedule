package com.shedule.zyx.myshedule.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothStateListener
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.managers.BluetoothManager
import com.shedule.zyx.myshedule.managers.BluetoothManager.OnConnectionListener
import com.shedule.zyx.myshedule.managers.ScheduleManager
import kotlinx.android.synthetic.main.fragment_list_of_devices.*
import org.jetbrains.anko.onItemClick
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

/**
 * Created by Alexander on 20.08.2016.
 */
class BondedDevicesFragment : Fragment(), OnConnectionListener {

  @Inject
  lateinit var bluetoothManager: BluetoothManager

  @Inject
  lateinit var scheduleManager: ScheduleManager
  lateinit var progressDialog: ProgressDialog

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ScheduleApplication.getComponent().inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
      inflater!!.inflate(R.layout.fragment_bonded_devices, container, false)

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val pairs = bluetoothManager.getPairedDevices().map { it }
    val names = pairs.map { it.second }
    list_of_devices.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, names)

    bluetoothManager.onConnectionListener = this

    list_of_devices.onItemClick { adapterView, view, i, l ->
      bluetoothManager.connect(pairs[i].first)
      progressDialog = indeterminateProgressDialog(getString(R.string.connecting))
      bluetoothManager.setStateListener(BluetoothStateListener { state ->
        if (state == bluetoothManager.STATE_CONNECTED) {
          progressDialog.hide()
          if (isAdded)
            selector("", listOf(getString(R.string.send), getString(R.string.cancel))) {
              when (it) {
                0 -> {
                  bluetoothManager.send(); bluetoothManager.disconnect()
                }
                1 -> {
                  bluetoothManager.disconnect()
                }
              }
            }
        } else if (state == bluetoothManager.STATE_CONNECTING)
          progressDialog.show()
      })
    }
  }

  override fun onConnectionState(state: Int) {
    if (state == bluetoothManager.STATE_CONNECTION_FAILED) {
      progressDialog.hide()
      if (isAdded)
        toast(getString(R.string.no_connection))
    }
  }
}