package com.shedule.zyx.myshedule.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.shedule.zyx.myshedule.models.ScheduleAdapterModel
import com.shedule.zyx.myshedule.models.ScheduleAdapterModel.Companion.ADD_NEW_SCHEDULE_TYPE
import com.shedule.zyx.myshedule.models.ScheduleAdapterModel.Companion.SCHEDULE_TYPE
import com.shedule.zyx.myshedule.widget.AddScheduleView
import com.shedule.zyx.myshedule.widget.ScheduleItemView

/**
 * Created by bogdan on 31.07.16.
 */
class ScheduleItemsAdapter(val context: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  var models = arrayListOf(ScheduleAdapterModel(ADD_NEW_SCHEDULE_TYPE))

  override fun getItemViewType(position: Int): Int {
    return when (models[position].type) {
      SCHEDULE_TYPE -> SCHEDULE_TYPE
      else -> ADD_NEW_SCHEDULE_TYPE
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return when (viewType) {
      SCHEDULE_TYPE -> ViewHolder(ScheduleItemView(context))
      else -> ViewHolder(AddScheduleView(context))
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
  }

  override fun getItemCount() = models.size

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

  fun setDatas(strings: List<String>) {
    models.add(ScheduleAdapterModel(SCHEDULE_TYPE))
    this.notifyDataSetChanged()
  }
}