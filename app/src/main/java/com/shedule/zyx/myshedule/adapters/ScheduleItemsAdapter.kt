package com.shedule.zyx.myshedule.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.shedule.zyx.myshedule.widget.ScheduleItemView

/**
 * Created by bogdan on 31.07.16.
 */
class ScheduleItemsAdapter(val context: Context) : RecyclerView.Adapter<ScheduleItemsAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(ScheduleItemView(context))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
  }

  override fun getItemCount() = 2

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    init { /* onClick */
    }
  }
}