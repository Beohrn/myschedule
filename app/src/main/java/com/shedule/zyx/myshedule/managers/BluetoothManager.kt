package com.shedule.zyx.myshedule.managers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import java.util.*

/**
 * Created by Alexander on 03.08.2016.
 */
class BluetoothManager constructor(bt : BluetoothSPP){

    private val btAdapter : BluetoothAdapter

    init {
        btAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    fun pairedDevices() : ArrayList<BluetoothDevice> {
        val result = ArrayList<BluetoothDevice>()
        btAdapter.bondedDevices.forEach {
            result.add(it)
        }
        return result
    }





}