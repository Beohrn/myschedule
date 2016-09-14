package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.models.Teacher
import com.shedule.zyx.myshedule.utils.Utils
import kotlinx.android.synthetic.main.teacher_view.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.include
import org.jetbrains.anko.onClick

/**
 * Created by alexkowlew on 06.09.2016.
 */
class TeacherView : FrameLayout {

  var onAssessmentClickListener: OnAssessmentClickListener? = null

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
    inflate(context, R.layout.teacher_view, this)
    setAssessment("E", 6.0)

    tv_assessment_of_teacher.onClick {
      showAssessments()
    }
  }

  fun setData(teacher: Teacher) {
    tv_name_of_teacher.text = teacher.nameOfTeacher
    tv_name_of_lesson.text = teacher.nameOfLesson
    setAssessment(teacher.assessmentString.toString(), teacher.averageAssessment ?: 60.0)
  }

  fun setAssessment(assessment: String, assessmentDouble: Double) {
    (tv_assessment_of_teacher.background as GradientDrawable)
        .setColor(Utils.getColorByAssessment(context, assessment))
    tv_assessment_of_teacher.text = assessment
    tv_average_assessment_of_teacher.text = assessmentDouble.toString()
  }

  fun setAssessmentWithListener(assessment: String, averageAssessment: Double) {
    setAssessment(assessment, averageAssessment)
    onAssessmentClickListener?.let {
      it.onAssessmentClick(assessment, tv_name_of_teacher.text.toString(),
          tv_average_assessment_of_teacher.text.toString().toDouble())
    }
  }

  fun showAssessments() {
    context.alert {
      customView {
        include<View>(R.layout.teacher_assessment_dialog) {
          val view = find<TeacherAssessmentView>(R.id.teacher_assessment_view)
          find<TextView>(R.id.at_ok).onClick {
            setAssessmentWithListener(Utils.getLetterByAverageAssessment(Utils.getAverageAssessment(view.getValues())),
                Utils.getAverageAssessment(view.getValues()))

            dismiss()
          }
          find<TextView>(R.id.at_cancel).onClick { dismiss() }
        }
      }
    }.show()
  }

  interface OnAssessmentClickListener {
    fun onAssessmentClick(assessment: String, teacherName: String, averageAssessment: Double)
  }
}