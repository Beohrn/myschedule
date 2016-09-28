package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.utils.Constants.Companion.ABILITY_TO_INFORMATION
import com.shedule.zyx.myshedule.utils.Constants.Companion.COMPETENCE
import com.shedule.zyx.myshedule.utils.Constants.Companion.DEMANDING
import com.shedule.zyx.myshedule.utils.Constants.Companion.DESIRE_TO_LEARN
import com.shedule.zyx.myshedule.utils.Constants.Companion.TEACHER_CULTURE
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

  fun getValues() = hashMapOf(COMPETENCE to competence.progress,
      DEMANDING to demanding.progress,
      ABILITY_TO_INFORMATION to ability_to_information.progress,
      DESIRE_TO_LEARN to desire_to_learn.progress,
      TEACHER_CULTURE to teacher_culture.progress)
}