package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.teacher_assessment_view.view.*

/**
 * Created by alexkowlew on 13.09.2016.
 */
class TeacherAssessmentView: FrameLayout {
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
    inflate(context, R.layout.teacher_assessment_view, this)
  }

  fun getValues() = hashMapOf("competence" to competence.progress,
      "demanding" to demanding.progress,
      "ability_to_information" to ability_to_information.progress,
      "desire_to_learn" to desire_to_learn.progress,
      "teacher_culture" to teacher_culture.progress)
}