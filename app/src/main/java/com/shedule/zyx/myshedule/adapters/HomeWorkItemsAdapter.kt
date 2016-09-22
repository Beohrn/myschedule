package com.shedule.zyx.myshedule.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.shedule.zyx.myshedule.models.HomeWork
import com.shedule.zyx.myshedule.widget.HomeWorkView
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLongClick
import java.util.*

/**
 * Created by alexkowlew on 15.09.2016.
 */
class HomeWorkItemsAdapter(val context: Context, val homeworkList: ArrayList<HomeWork>,
                           val onStatusChangeListener: HomeWorkView.OnStatusChangeListener) : RecyclerView.Adapter<HomeWorkItemsAdapter.ViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ViewHolder(HomeWorkView(context))

  override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
    (holder?.itemView as HomeWorkView).setData(homeworkList[position])
    holder?.itemView.onStatusChangeListener = onStatusChangeListener
  }

  override fun getItemCount() = homeworkList.size

  inner class ViewHolder(itemView: HomeWorkView) : RecyclerView.ViewHolder(itemView) {
    init {
      itemView.onClick { (context as OnHomeWorkClickListener).onHomeWorkClick(homeworkList[adapterPosition]) }
      itemView.onLongClick { (context as OnHomeWorkLongClickListener).onLongClick(homeworkList[adapterPosition]); true }
    }
  }

  interface OnHomeWorkClickListener {
    fun onHomeWorkClick(homeWork: HomeWork)
  }

  interface OnHomeWorkLongClickListener {
    fun onLongClick(homeWork: HomeWork)
  }
}