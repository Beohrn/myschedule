package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.models.Teacher
import com.shedule.zyx.myshedule.utils.Utils
import kotlinx.android.synthetic.main.teacher_view.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.selector

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
//    tv_assessment_of_teacher.setOnClickListener(this)
    setAssessment("E")

//    tv_container.onClick {  }
    tv_assessment_of_teacher.onClick {
      showAssessments()
    }
  }


  fun setData(teacher: Teacher) {
    tv_name_of_teacher.text = teacher.nameOfTeacher
    tv_name_of_lesson.text = teacher.nameOfLesson
    setAssessment(teacher.assessment.toString())
  }

  fun setAssessment(assessment: String) {
    (tv_assessment_of_teacher.background as GradientDrawable)
        .setColor(Utils.getColorByAssessment(context, assessment))
    tv_assessment_of_teacher.text = assessment
  }

  fun setAssessmentWithListener(assessment: String) {
    setAssessment(assessment)
    onAssessmentClickListener?.let {
      it.onAssessmentClick(assessment, tv_name_of_teacher.text.toString())
    }
  }

//  override fun onClick(v: View?) {
//    when (v?.id) {
////      R.id.tv_assessment_of_teacher ->  showAssessments()
////      R.id.tv_container -> context.toast("Teacher")
//    }
//  }

  fun showAssessments() {
    context.selector("Как вы оцениваете преподавателя?",
        listOf("Отлично", "Хорошо", "Нормально", "Так себе", "Плохо")) { position ->
      when (position) {
        0 -> setAssessmentWithListener("A")
        1 -> setAssessmentWithListener("B")
        2 -> setAssessmentWithListener("C")
        3 -> setAssessmentWithListener("D")
        4 -> setAssessmentWithListener("E")
      }
    }
  }

  interface OnAssessmentClickListener {
    fun onAssessmentClick(assessment: String, teacherName: String)
  }
}

interface TeacherClickListener {
  fun onClick()
}