package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.models.HomeWork
import kotlinx.android.synthetic.main.homework_view.view.*

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
    inflate(context, R.layout.homework_view, this)
    status_of_homework.setOnCheckedChangeListener { compoundButton, b ->
      onStatusChangeListener?.let {
        it.onChangeStatus(b, home_work_task_name.text.toString())
      }
    }
  }

  fun setData(homework: HomeWork) {
    home_work_task_name.text = homework.taskName
    status_of_homework.isChecked = homework.status
  }

  interface OnStatusChangeListener {
    fun onChangeStatus(status: Boolean, taskName: String)
  }
}