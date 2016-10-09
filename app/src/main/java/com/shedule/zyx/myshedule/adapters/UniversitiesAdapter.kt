package com.shedule.zyx.myshedule.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.onClick

/**
 * Created by alexkowlew on 05.10.2016.
 */
class UniversitiesAdapter(val context: Context, val list: List<String>): RecyclerView.Adapter<UniversitiesAdapter.ViewHolder>() {
  override fun getItemCount() = list.size

  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
    ViewHolder(TextView(context))

  override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
    (holder?.itemView as TextView).text = list[position]
  }


  inner class ViewHolder(itemView: TextView): RecyclerView.ViewHolder(itemView) {
    init {
      itemView.onClick { (context as OnItemClickListener).onItemClick(list[adapterPosition]) }
    }
  }

  interface OnItemClickListener {
    fun onItemClick(university: String)
  }
}