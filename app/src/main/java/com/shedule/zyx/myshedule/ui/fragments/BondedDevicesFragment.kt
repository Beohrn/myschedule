package com.shedule.zyx.myshedule.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothStateListener
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.managers.BluetoothManager.OnConnectionListener
import kotlinx.android.synthetic.main.fragment_list_of_devices.*
import org.jetbrains.anko.onItemClick
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.toast

/**
 * Created by Alexander on 20.08.2016.
 */
class BondedDevicesFragment : BaseFragment(), OnConnectionListener {

  override var contentView = R.layout.fragment_bonded_devices
  private var progressDialog: ProgressDialog? = null

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val pairs = bluetoothManager.getPairedDevices().map { it }
    val names = pairs.map { it.second }
    list_of_devices.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, names)

    bluetoothManager.onConnectionListener = this

    list_of_devices.onItemClick { adapterView, view, i, l ->
      showDialog(getString(R.string.connecting))
      bluetoothManager.connect(pairs[i].first)
      bluetoothManager.setStateListener(BluetoothStateListener { state ->
        if (state == bluetoothManager.STATE_CONNECTED) {
          hideDialog()
          if (isAdded)
            selector("", listOf(getString(R.string.send), getString(R.string.cancel))) {
              when (it) {
                0 -> { bluetoothManager.send(); bluetoothManager.disconnect() }
                1 -> bluetoothManager.disconnect()
              }
            }
        } else if (state == bluetoothManager.STATE_CONNECTING) hideDialog()
      })
    }
  }

  override fun onConnectionState(state: Int) {
    if (state == bluetoothManager.STATE_CONNECTION_FAILED) {
      hideDialog()
      if (isAdded)
        toast(getString(R.string.no_connection))
    }
  }
}