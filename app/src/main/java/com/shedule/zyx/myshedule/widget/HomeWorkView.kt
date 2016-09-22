package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.util.AttributeSet
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.models.HomeWork
import com.shedule.zyx.myshedule.utils.Utils
import kotlinx.android.synthetic.main.homework_view_for_single_lesson.view.*
import org.jetbrains.anko.image
import org.jetbrains.anko.onClick

/**
 * Created by alexkowlew on 15.09.2016.
 */
class HomeWorkView: FrameLayout {

  var onStatusChangeListener: OnStatusChangeListener? = null

  constructor(context: Context?) : super(context) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    init(context)
  }

  fun init(context: Context?) {
    inflate(context, R.layout.homework_view_for_single_lesson, this)
    status_of_homework.setOnCheckedChangeListener { compoundButton, b ->
      onStatusChangeListener?.let {
        it.onChangeStatus(b, home_work_task_name.text.toString())
      }
    }
    expandableTextView.setAnimationDuration(750L)
    expandableTextView.setInterpolator(OvershootInterpolator())

    expand.onClick {
      expandableTextView.toggle()
      if (expandableTextView.isExpanded) {
        expandableTextView.collapse()
        expand.image = resources.getDrawable(R.drawable.arrow_down)
      } else {
        expandableTextView.expand()
        expand.image = resources.getDrawable(R.drawable.arrow_up)
      }
    }
  }

  fun setData(homework: HomeWork) {
    expandableTextView.collapse()
    home_work_task_name.text = homework.taskName
    status_of_homework.isChecked = homework.status
    expandableTextView.text = homework.taskDescription
    dead_line.text = Utils.convertDateString(homework.deadLine)
  }

  interface OnStatusChangeListener {
    fun onChangeStatus(status: Boolean, taskName: String)
  }
}