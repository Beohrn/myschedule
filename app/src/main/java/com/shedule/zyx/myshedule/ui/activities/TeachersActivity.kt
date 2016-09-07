package com.shedule.zyx.myshedule.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.adapters.TeachersAdapter
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.widget.TeacherView
import kotlinx.android.synthetic.main.teachers_activity.*
import javax.inject.Inject

/**
 * Created by alexkowlew on 05.09.2016.
 */
class TeachersActivity: AppCompatActivity(), TeacherView.OnAssessmentClickListener {

  @Inject
  lateinit var scheduleManager: ScheduleManager
  val TAG = this@TeachersActivity.javaClass.simpleName

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.teachers_activity)
    ScheduleApplication.getComponent().inject(this)

    setSupportActionBar(teachers_toolbar)
    supportActionBar?.title = "Преподаватели"
    teachers_toolbar.setTitleTextColor(Color.WHITE)
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    teachers_toolbar.setNavigationOnClickListener { finish() }
    val adapter = TeachersAdapter(this@TeachersActivity, scheduleManager.getTeachers(), this)
    list_of_teachers.layoutManager = LinearLayoutManager(applicationContext)
    list_of_teachers.itemAnimator = DefaultItemAnimator()
    list_of_teachers.adapter = adapter
  }

  // todo
  override fun onAssessmentClick(assessment: String, teacherName: String) {
    Log.i(TAG, "$assessment - $teacherName")
  }
}