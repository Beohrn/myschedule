package com.shedule.zyx.myshedule.managers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import org.jetbrains.anko.newTask
import java.util.*

/**
 * Created by Alexander on 03.08.2016.
 */
class BluetoothManager constructor(var bt: BluetoothSPP, var context: Context){

    private val TAG = BluetoothManager::class.java.simpleName
    val btAdapter: BluetoothAdapter

    val ACTION_ENABLE = BluetoothAdapter.ACTION_REQUEST_ENABLE
    val REQUEST_ENABLE = BluetoothState.REQUEST_ENABLE_BT

    init {
        btAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    private fun bondedDevices(): ArrayList<BluetoothDevice> {
        val result = ArrayList<BluetoothDevice>()
        btAdapter.bondedDevices.forEach {
            result.add(it)
        }
        return result
    }

    fun getDevices(): ArrayList<String> {
        val names = ArrayList<String>()
        bondedDevices().forEach {
            names.add(it.name)
        }

        return names
    }

    fun connect(index: Int) {
        val address = bondedDevices()[index].address
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

    fun serviceAvailable(): Boolean {
        if (bt.isServiceAvailable)
            return true
        else
            return false
    }

    fun setupService() {

        bt.setupService()
        bt.startService(BluetoothState.DEVICE_ANDROID)

    }

    fun stopService() {
        if (bt.isServiceAvailable)
            bt.stopService()
    }

    fun bluetoothEnabled() : Boolean {
        if (btAdapter.isEnabled)
            return true
        else
            return false
    }

//    fun onBluetooth() {
//        if (!btAdapter.isEnabled)
//            btAdapter.enable()
//    }
//
//    fun offBluetooth() {
//        if (btAdapter.isEnabled)
//            btAdapter.disable()
//    }

    fun nearbyDevices() {
        if (btAdapter.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            makeDiscoverable()
        }

        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(receiver, filter)
        btAdapter.startDiscovery()

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                context!!.unregisterReceiver(this)
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                val devices = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                Log.d("TAG", "${devices.name}")
            }
        }
    }

    private fun makeDiscoverable() {
        val discoverable = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        discoverable.newTask()
        context.startActivity(discoverable)
    }
}