package com.shedule.zyx.myshedule.ui.fragments

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
import com.shedule.zyx.myshedule.managers.DateManager
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.models.Schedule
import kotlinx.android.synthetic.main.shedule_fragment_layout.*
import javax.inject.Inject

/**
 * Created by bogdan on 20.07.16.
 */
class ScheduleFragment : Fragment(), DataChangeListener {

  @Inject
  lateinit var scheduleManager: ScheduleManager

  @Inject
  lateinit var dateManager: DateManager

  val ARGUMENT_PAGE_NUMBER = "arg_page_number"

  lateinit var adapter: ScheduleItemsAdapter

  var position = -1
  var listSchedulers = arrayListOf<Schedule>()

  fun newInstance(page: Int): ScheduleFragment {
    val pageFragment = ScheduleFragment()
    val arguments = Bundle()
    arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
    pageFragment.arguments = arguments
    return pageFragment
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    ScheduleApplication.getComponent().inject(this)
    position = arguments.getInt(ARGUMENT_PAGE_NUMBER)

    update()

    (activity as ChangeStateFragmentListener).let { Log.d("listener", "add to list"); it.addListener(this) }
    return inflater!!.inflate(R.layout.shedule_fragment_layout, container, false)
  }

  private fun update() {
    listSchedulers.clear()
    listSchedulers.addAll(scheduleManager.getScheduleByDay(dateManager.getDayByPosition(position)))
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    list_schedules.layoutManager = LinearLayoutManager(activity)
    list_schedules.itemAnimator = DefaultItemAnimator()

    adapter = ScheduleItemsAdapter(context, listSchedulers)
    list_schedules.adapter = adapter

    Log.d("1111", dateManager.getDayByPosition(position))
  }

  override fun updateData() {
    Log.d("1111", dateManager.getDayByPosition(position))
    update()
    adapter.notifyDataSetChanged()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    (activity as ChangeStateFragmentListener).let { Log.d("listener", "remove from list"); it.removeListener(this) }
  }
}