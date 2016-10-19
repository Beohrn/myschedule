package com.shedule.zyx.myshedule.events

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.shedule.zyx.myshedule.events.EventsRecycleAdapter.EventViewHolder
import com.shedule.zyx.myshedule.widget.EventView

/**
 * Created by bogdan on 08.10.16.
 */
class EventsRecycleAdapter(val context: Context, val list: List<Event>?, val listener: EventView.LinkListener) : RecyclerView.Adapter<EventViewHolder>() {

  override fun onBindViewHolder(holder: EventViewHolder?, position: Int) {
    list?.let {
      (holder?.itemView as EventView).let {
        it.listener = listener
        it.setData(list[position])
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
    return EventViewHolder(EventView(context))
  }

  override fun getItemCount() = if (list == null) 0 else list.size

  inner class EventViewHolder(itemView: EventView) : RecyclerView.ViewHolder(itemView) {

  }
}