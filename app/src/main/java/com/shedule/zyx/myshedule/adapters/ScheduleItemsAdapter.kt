package com.shedule.zyx.myshedule.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.widget.ScheduleItemView
import org.jetbrains.anko.onClick
import java.util.*

/**
 * Created by bogdan on 31.07.16.
 */
class ScheduleItemsAdapter(val context: Context, val list: ArrayList<Schedule>) : RecyclerView.Adapter<ScheduleItemsAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(ScheduleItemView(context))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    (holder.itemView as ScheduleItemView).setData(list[position])
  }

  override fun getItemCount() = list.size


  inner class ViewHolder(itemView: ScheduleItemView) : RecyclerView.ViewHolder(itemView) {
    init { itemView.onClick { (context as ScheduleItemListener).scheduleItemClick(list[adapterPosition]) } }
  }
  interface ScheduleItemListener {
    fun scheduleItemClick(schedule: Schedule)
  }
}