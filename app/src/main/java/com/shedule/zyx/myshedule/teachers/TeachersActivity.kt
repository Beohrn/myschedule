package com.shedule.zyx.myshedule.teachers

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import app.voter.xyz.auth.ReplaceFragmentListener
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.auth.fragments.CreateAccountFragment
import com.shedule.zyx.myshedule.comments.DiscussionActivity
import com.shedule.zyx.myshedule.comments.DiscussionActivity.Companion.TEACHER_REQUEST
import com.shedule.zyx.myshedule.models.Teacher
import com.shedule.zyx.myshedule.teachers.TeachersAdapter.OnTeacherClickListener
import com.shedule.zyx.myshedule.ui.activities.BaseActivity
import com.shedule.zyx.myshedule.widget.TeacherView.OnRatingClickListener
import kotlinx.android.synthetic.main.teachers_activity.*
import org.jetbrains.anko.startActivity

/**
 * Created by alexkowlew on 05.09.2016.
 */
class TeachersActivity: BaseActivity(), OnRatingClickListener, OnTeacherClickListener, ReplaceFragmentListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(R.layout.teachers_activity)

    setSupportActionBar(teachers_toolbar)
    supportActionBar?.title = getString(R.string.teachers)
    teachers_toolbar.setTitleTextColor(Color.WHITE)
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    teachers_toolbar.setNavigationOnClickListener { finish() }

    if (auth.currentUser?.uid == null) replaceFragment(getString(R.string.anonymous_authentication), CreateAccountFragment())
    else replaceFragment(getString(R.string.teachers), TeachersRatingFragment())
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
    firebaseWrapper.pushRating(averageAssessment, teacherName)
  }
}