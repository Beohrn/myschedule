package com.shedule.zyx.myshedule.managers

import android.content.Context
import android.util.Log
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import org.jetbrains.anko.toast

/**
 * Created by Alexander on 07.08.2016.
 */
class BTConnectionManager constructor(var context: Context): BluetoothSPP.BluetoothConnectionListener {

    private val TAG = BTConnectionManager::class.java.simpleName

    override fun onDeviceDisconnected() {
        Log.d(TAG, "Device disconnected")
        context.toast("Device disconnected")
    }

    override fun onDeviceConnected(name: String?, address: String?) {
        Log.d(TAG, "Connected to $name -> $address")
        context.toast("Connected to $name -> $address")

    }

    override fun onDeviceConnectionFailed() {
        Log.wtf(TAG, "Connection failed")
        context.toast("Connection failed")
    }

}