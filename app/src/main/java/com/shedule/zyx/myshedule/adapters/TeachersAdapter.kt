package com.shedule.zyx.myshedule.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import app.voter.xyz.comments.DiscussionActivity
import app.voter.xyz.comments.DiscussionActivity.Companion.TEACHER_REQUEST
import com.shedule.zyx.myshedule.models.Teacher
import com.shedule.zyx.myshedule.widget.TeacherView
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

/**
 * Created by alexkowlew on 06.09.2016.
 */
class TeachersAdapter(val context: Context,
                      val list: List<Teacher>,
                      val assessmentListener: TeacherView.OnAssessmentClickListener): RecyclerView.Adapter<TeachersAdapter.ViewHolder>() {

  override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
    (holder?.itemView as TeacherView).setData(list[position])
    holder?.itemView.onAssessmentClickListener = assessmentListener
  }

  override fun getItemCount() = list.size

  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ViewHolder(TeacherView(context))

  inner class ViewHolder(itemView: TeacherView) : RecyclerView.ViewHolder(itemView) {
    init {
      itemView.onClick {
        if (list[adapterPosition].nameOfTeacher.isNotEmpty())
          context.startActivity<DiscussionActivity>(TEACHER_REQUEST to list[adapterPosition].nameOfTeacher)
      }
    }
  }
}