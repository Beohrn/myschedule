package com.shedule.zyx.myshedule.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.interfaces.ChangeStateFragmentListener
import com.shedule.zyx.myshedule.interfaces.DataChangeListener
import java.util.*

/**
 * Created by bogdan on 20.07.16.
 */
class ScheduleFragment : Fragment(), DataChangeListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (activity as ChangeStateFragmentListener).let { it.addListener(this) }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater!!.inflate(R.layout.shedule_fragment_layout, container, false)
  }

  override fun updateData(calendar: Calendar) {

  }

  override fun onDestroyView() {
    super.onDestroyView()
    (activity as ChangeStateFragmentListener).let { it.removeListener(this) }
  }
}