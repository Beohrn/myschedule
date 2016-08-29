package com.shedule.zyx.myshedule.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.fragment_list_of_devices.*
import kotlinx.android.synthetic.main.fragment_nearby_devices.*

/**
 * Created by Alexander on 20.08.2016.
 */
class NearbyDevicesFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
      inflater!!.inflate(R.layout.fragment_nearby_devices, container, false)

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    search_devices.setOnRefreshListener {

    }

    list_of_devices.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, arrayListOf("F", "G", "E", "K", "L"))

  }
}