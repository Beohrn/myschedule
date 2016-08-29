package com.shedule.zyx.myshedule.ui.fragments

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
class BondedDevicesFragment : Fragment() {

    @Inject
    lateinit var bluetoothManager: BluetoothManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScheduleApplication.getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_bonded_devices, container, false)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = bluetoothManager.getDevices()
        list_of_devices.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list)
        list_of_devices.onItemClick { adapterView, view, i, l ->
            bluetoothManager.connect(i, true)
        }

    }
}