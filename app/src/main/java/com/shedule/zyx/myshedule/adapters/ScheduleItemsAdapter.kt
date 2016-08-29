package com.shedule.zyx.myshedule.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.shedule.zyx.myshedule.widget.ScheduleItemView
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

/**
 * Created by bogdan on 31.07.16.
 */
class ScheduleItemsAdapter(val context: Context) : RecyclerView.Adapter<ScheduleItemsAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(ScheduleItemView(context))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
  }

  override fun getItemCount() = 30

  inner class ViewHolder(itemView: ScheduleItemView) : RecyclerView.ViewHolder(itemView) {

    init {
      itemView.onClick {
        itemView.onClick()
      }
    }

    fun delete() {
      context.toast("delete $adapterPosition")
    }
  }
}