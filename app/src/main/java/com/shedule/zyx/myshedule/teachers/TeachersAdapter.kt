package com.shedule.zyx.myshedule.teachers

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.models.Teacher
import com.shedule.zyx.myshedule.widget.TeacherView
import com.shedule.zyx.myshedule.widget.TeacherView.OnRatingClickListener
import org.jetbrains.anko.onClick

/**
 * Created by bogdan on 16.09.16.
 */
class TeachersAdapter(val context: Context, val ref: DatabaseReference,
                      val listener: OnTeacherClickListener, val ratingListener: OnRatingClickListener) :
    FirebaseRecyclerAdapter<Teacher, TeachersAdapter.ViewHolder>(Teacher::class.java,
        R.layout.teacher_adapter_item, ViewHolder::class.java, ref) {

  override fun populateViewHolder(viewHolder: ViewHolder?, model: Teacher?, position: Int) {
    model?.let {
      (viewHolder?.itemView as TeacherView).setData(model)
      viewHolder?.itemView.ratingClickListener = ratingListener
      viewHolder?.view?.onClick { listener.onTeacherClick(model) }
    }
  }

  class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

  interface OnTeacherClickListener {
    fun onTeacherClick(teacher: Teacher?)
  }
}