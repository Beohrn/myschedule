package com.shedule.zyx.myshedule.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.adapters.ScheduleRecycleAdapter
import com.shedule.zyx.myshedule.interfaces.ChangeStateFragmentListener
import com.shedule.zyx.myshedule.interfaces.DataChangeListener
import com.shedule.zyx.myshedule.managers.DateManager
import kotlinx.android.synthetic.main.shedule_fragment_layout.*
import javax.inject.Inject

/**
 * Created by bogdan on 20.07.16.
 */
class ScheduleFragment : Fragment(), DataChangeListener {

  @Inject
  lateinit var dateManager: DateManager
  var position = 0

  fun newInstance(position: Int): Fragment {
    val myFragment = ScheduleFragment()
    val bundle = Bundle()
    bundle.putInt("position", position)
    myFragment.arguments = bundle
    return myFragment
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ScheduleApplication.getComponent().inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    (activity as ChangeStateFragmentListener).let { it.addListener(this) }
    return inflater!!.inflate(R.layout.shedule_fragment_layout, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    position = arguments.getInt("position", 0)
    recycle_list.layoutManager = LinearLayoutManager(activity)
    recycle_list.adapter = ScheduleRecycleAdapter(activity)
  }

  override fun updateData() {
    dateManager.getDayByPosition(position)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    (activity as ChangeStateFragmentListener).let { it.removeListener(this) }
  }
}