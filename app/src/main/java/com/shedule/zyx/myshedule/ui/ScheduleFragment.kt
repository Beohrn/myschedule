package com.shedule.zyx.myshedule.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.interfaces.ChangeStateFragmentListener
import com.shedule.zyx.myshedule.interfaces.DataChangeListener
import kotlinx.android.synthetic.main.shedule_fragment_layout.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by bogdan on 20.07.16.
 */
class ScheduleFragment : Fragment(), DataChangeListener {

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    (activity as ChangeStateFragmentListener).let { Log.d("listener", "add to list"); it.addListener(this) }
    return inflater!!.inflate(R.layout.shedule_fragment_layout, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    add_schedule_btn.onClick {
      startActivity<ScheduleInformationActivity>()
    }
  }

  override fun updateData(string: String) {

  }

  override fun onDestroyView() {
    super.onDestroyView()
    (activity as ChangeStateFragmentListener).let { Log.d("listener", "remove from list"); it.removeListener(this) }
  }
}