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
import com.shedule.zyx.myshedule.utils.Utils.Companion.getLetterByRating
import kotlinx.android.synthetic.main.teacher_view.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.include
import org.jetbrains.anko.onClick

/**
 * Created by alexkowlew on 06.09.2016.
 */
class TeacherView : FrameLayout {

  lateinit var ratingClickListener: OnRatingClickListener
  lateinit var teacher: Teacher

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
    teacher_rating.onClick { showAssessments() }
  }

  fun setData(teacher: Teacher) {
    this.teacher = teacher
    teacher_name.text = teacher.teacherName
    tv_name_of_lesson.text = teacher.lessonName

    //default value and color
    setColorByRating(60.toDouble())

    if (teacher.ratings.isNotEmpty()) {
      val mediumRating = teacher.ratings.values.toList().reduce { d1, d2 -> d1 + d2 } /
          teacher.ratings.values.toList().size

      setColorByRating(mediumRating)
      teacher_rating.text = getLetterByRating(mediumRating)
      rating.text = "$mediumRating"
    }
  }

  private fun setColorByRating(mediumRating: Double) {
    (teacher_rating.background as GradientDrawable)
        .setColor(Utils.getColorByRating(context, getLetterByRating(mediumRating)))
  }

  fun showAssessments() {
    context.alert {
      customView {
        include<View>(R.layout.teacher_rating_dialog) {
          val view = find<TeacherAssessmentView>(R.id.teacher_assessment_view)
          find<TextView>(R.id.at_ok).onClick {
            ratingClickListener.onRatingClick(teacher.teacherName, Utils.getRatingByData(view.getValues()))
            dismiss()
          }
          find<TextView>(R.id.at_cancel).onClick { dismiss() }
        }
      }
    }.show()
  }

  interface OnRatingClickListener {
    fun onRatingClick(teacherName: String, averageAssessment: Double)
  }
}