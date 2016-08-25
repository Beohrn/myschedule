package com.shedule.zyx.myshedule.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.shedule.zyx.myshedule.models.ScheduleAdapterModel
import com.shedule.zyx.myshedule.models.ScheduleAdapterModel.Companion.SCHEDULE_TYPE
import com.shedule.zyx.myshedule.widget.ScheduleItemView

/**
 * Created by bogdan on 07.08.16.
 */
// todo need to think about use listview or recycleview
class ScheduleListAdapter(val context: Context) : BaseAdapter() {
  var models = arrayListOf(ScheduleAdapterModel(ScheduleAdapterModel.ADD_NEW_SCHEDULE_TYPE))

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    return ScheduleItemView(context)
  }

  override fun getItem(position: Int) = models[position]

  override fun getItemId(position: Int) = 0.toLong()

  override fun getCount() = models.size

  fun setDatas(strings: List<String>) {
    models.add(ScheduleAdapterModel(SCHEDULE_TYPE))
    this.notifyDataSetChanged()
  }
}