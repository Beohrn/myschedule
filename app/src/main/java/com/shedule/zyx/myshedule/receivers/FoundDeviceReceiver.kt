package com.shedule.zyx.myshedule.receivers

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.shedule.zyx.myshedule.interfaces.FoundDeviceReceiverCallback

/**
 * Created by Alexander on 23.08.2016.
 */
class FoundDeviceReceiver(val callback: FoundDeviceReceiverCallback): BroadcastReceiver() {

//    private val callback: FoundDeviceReceiverCallback

    fun register(context: Context?) {
        context!!.registerReceiver(this, getIntentFilter())
    }

    private fun getIntentFilter() = IntentFilter(BluetoothDevice.ACTION_FOUND)

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            callback.onDeviceFound(device)
        }
    }

    fun unregister(context: Context?, foundDeviceReceiver: FoundDeviceReceiver) {
        context!!.unregisterReceiver(foundDeviceReceiver)
    }
}