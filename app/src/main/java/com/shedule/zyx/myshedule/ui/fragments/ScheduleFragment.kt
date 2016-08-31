package com.shedule.zyx.myshedule.ui.fragments

import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.adapters.ScheduleItemsAdapter
import com.shedule.zyx.myshedule.interfaces.ChangeStateFragmentListener
import com.shedule.zyx.myshedule.interfaces.DataChangeListener
import com.shedule.zyx.myshedule.managers.ScheduleManager
import kotlinx.android.synthetic.main.shedule_fragment_layout.*
import javax.inject.Inject

/**
 * Created by bogdan on 20.07.16.
 */
class ScheduleFragment : Fragment(), DataChangeListener {

  @Inject
  lateinit var scheduleManager: ScheduleManager

  lateinit var adapter: ScheduleItemsAdapter
  val p = Paint()

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    ScheduleApplication.getComponent().inject(this)
    (activity as ChangeStateFragmentListener).let { Log.d("listener", "add to list"); it.addListener(this) }
    return inflater!!.inflate(R.layout.shedule_fragment_layout, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    list_schedules.layoutManager = LinearLayoutManager(activity)
    list_schedules.itemAnimator = DefaultItemAnimator()

    adapter = ScheduleItemsAdapter(context, scheduleManager.globalList)
    list_schedules.adapter = adapter
//    add_schedule_btn.onClick {
//      startActivity<ScheduleInformationActivity>()
//    }
  }

  override fun updateData(string: String) {
    Log.i("TDADFA", string)

  }

  override fun onDestroyView() {
    super.onDestroyView()
    (activity as ChangeStateFragmentListener).let { Log.d("listener", "remove from list"); it.removeListener(this) }
  }
}