package com.shedule.zyx.myshedule.managers

import android.content.Context
import android.util.Log
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import org.jetbrains.anko.toast

class ReceiveManager constructor(var context : Context): BluetoothSPP.OnDataReceivedListener {

    private val TAG = ReceiveManager::class.java.name

    override fun onDataReceived(data: ByteArray?, message: String?) {
        Log.d(TAG, message)
        context.toast("$message")
        /*some logic*/
    }

}