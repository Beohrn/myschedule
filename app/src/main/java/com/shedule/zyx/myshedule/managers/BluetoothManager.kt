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
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by Alexander on 03.08.2016.
 */
class BluetoothManager constructor(var bt: BluetoothSPP, var context: Context) :
        BluetoothSPP.OnDataReceivedListener,
        BluetoothSPP.BluetoothConnectionListener,
        BluetoothSPP.BluetoothStateListener {


    private val TAG = "BluetoothManager"
    val btAdapter: BluetoothAdapter

    override fun onServiceStateChanged(state: Int) {

    }

    override fun onDeviceDisconnected() {
        Log.d(TAG, "Device disconnected")
    }

    override fun onDeviceConnected(name: String?, address: String?) {
        Log.d(TAG, "Connected to $name -> $address")
        context.toast("Connected to $name -> $address")
//        send("TEXT")

    }

    override fun onDeviceConnectionFailed() {
        Log.wtf(TAG, "Connection failed")
    }


    override fun onDataReceived(data: ByteArray?, message: String?) {
        context.toast("$message")
        Log.d(TAG, message)
    }

    init {
        btAdapter = BluetoothAdapter.getDefaultAdapter()
//        bt.setupService()
//        bt.startService(BluetoothState.DEVICE_ANDROID)
    }

    private fun bondedDevices(): ArrayList<BluetoothDevice> {
//        setupService()
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
        setupService()
        bt.connect(address)

        bt.setBluetoothConnectionListener(this)
    }

    fun send(message: String) {
        bt.send(message, true)
        bt.setOnDataReceivedListener(this)
    }

    fun setupService() {
        if (!bt.isServiceAvailable) {
            bt.setupService()
            bt.startService(BluetoothState.DEVICE_ANDROID)
        }
    }

    fun stopService() {
        if (bt.isServiceAvailable)
            bt.stopService()
    }

    fun onBluetooth() {
        if (!btAdapter.isEnabled)
            btAdapter.enable()
    }

    fun offBluetooth() {
        if (btAdapter.isEnabled)
            btAdapter.disable()
    }

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

//        context.unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
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