package com.shedule.zyx.myshedule.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.widget.AllHomeWokrsView
import org.jetbrains.anko.onClick
import java.util.*

/**
 * Created by alexkowlew on 21.09.2016.
 */
class AllHomeWorkItemsAdapter(val context: Context, val list: ArrayList<Schedule>): RecyclerView.Adapter<AllHomeWorkItemsAdapter.ViewHolder>() {

  override fun getItemCount() = list.size

  override fun onBindViewHolder(holder: ViewHolder?, position: Int) =
      (holder?.itemView as AllHomeWokrsView).setData(list[position])

  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ViewHolder(AllHomeWokrsView(context))

  inner class ViewHolder(itemView: AllHomeWokrsView): RecyclerView.ViewHolder(itemView) {
    init { itemView.onClick { (context as OnItemClick).onClick(list[adapterPosition]) } }
  }

  interface OnItemClick { fun onClick(schedule: Schedule) }
}