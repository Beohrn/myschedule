package com.shedule.zyx.myshedule.teachers

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import app.voter.xyz.auth.ReplaceFragmentListener
import app.voter.xyz.auth.fragments.CreateAccountFragment
import app.voter.xyz.auth.fragments.TeachersRatingFragment
import app.voter.xyz.comments.DiscussionActivity
import app.voter.xyz.comments.DiscussionActivity.Companion.TEACHER_REQUEST
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.models.Teacher
import com.shedule.zyx.myshedule.teachers.TeachersAdapter.OnTeacherClickListener
import com.shedule.zyx.myshedule.widget.TeacherView.OnRatingClickListener
import kotlinx.android.synthetic.main.teachers_activity.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * Created by alexkowlew on 05.09.2016.
 */
class TeachersActivity : AppCompatActivity(), OnRatingClickListener, OnTeacherClickListener, ReplaceFragmentListener {

  @Inject
  lateinit var scheduleManager: ScheduleManager

  @Inject
  lateinit var auth: FirebaseAuth

  @Inject
  lateinit var ref: DatabaseReference

  @Inject
  lateinit var firebase: FirebaseWrapper

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

    if (auth.currentUser?.uid == null) replaceFragment("Анонимная авторизация", CreateAccountFragment())
    else replaceFragment("Преподаватели", TeachersRatingFragment())
  }

  private fun replaceFragment(title: String, fragment: Fragment?) {
    supportActionBar?.title = title
    fragment?.let {
      supportFragmentManager.beginTransaction().replace(R.id.account_container_fragments, it)
          .addToBackStack(title).commit()
    }
  }

  override fun onBackPressed() = finish()

  override fun changeVisibleFragment(title: String, fragment: Fragment?) = replaceFragment(title, fragment)

  override fun onTeacherClick(teacher: Teacher?) {
    teacher?.let {
      startActivity<DiscussionActivity>(TEACHER_REQUEST to it.teacherName)
    }
  }

  override fun onRatingClick(teacherName: String, averageAssessment: Double) {
    firebase.pushRating(averageAssessment, teacherName)
  }
}