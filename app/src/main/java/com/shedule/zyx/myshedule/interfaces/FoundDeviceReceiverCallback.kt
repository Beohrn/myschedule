package com.shedule.zyx.myshedule.interfaces

import android.bluetooth.BluetoothDevice

/**
 * Created by Alexander on 23.08.2016.
 */
interface FoundDeviceReceiverCallback {
    fun onDeviceFound(device: BluetoothDevice)
}