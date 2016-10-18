package com.shedule.zyx.myshedule.teachers

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import app.voter.xyz.RxFirebase
import com.google.firebase.crash.FirebaseCrash.report
import com.shedule.zyx.myshedule.BuildConfig.DEBOUG_ENABLED
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.teachers.TeachersAdapter.OnTeacherClickListener
import com.shedule.zyx.myshedule.ui.fragments.BaseFragment
import com.shedule.zyx.myshedule.utils.Constants.Companion.EMPTY_DATA
import com.shedule.zyx.myshedule.utils.Utils.Companion.isOnline
import com.shedule.zyx.myshedule.utils.toMainThread
import com.shedule.zyx.myshedule.widget.TeacherView.OnRatingClickListener
import kotlinx.android.synthetic.main.teachers_rating_layout.*
import org.jetbrains.anko.support.v4.toast

/**
 * Created by bogdan on 13.09.16.
 */
class TeachersRatingFragment : BaseFragment() {

  override var contentView = R.layout.teachers_rating_layout

  lateinit var teachersAdapter: TeachersAdapter

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    toggleEmptyTeachers(false)
    no_connection.visibility = GONE

    showDialog(getString(R.string.load))
    if (isOnline(context))
      firebaseWrapper.pushTeacher(scheduleManager.getTeachers().filter { it.teacherName.isNotEmpty() })
          .toMainThread()
          .subscribe({
            hideDialog()
            if (it) toggleEmptyTeachers(false) else
              toggleEmptyTeachers(true)
          }, {
            if (DEBOUG_ENABLED) report(it)
            hideDialog()
            if (it.message == EMPTY_DATA) toggleEmptyTeachers(true)
            else toast(getString(R.string.download_error))
          })
    else noInternetConnection()

    RxFirebase.observeChildAdded(firebaseWrapper.createTeacherRef()).subscribe({
      teachers_recycle_view.smoothScrollToPosition(teachersAdapter.itemCount)
    }, {})

    teachersAdapter = TeachersAdapter(context, firebaseWrapper.createTeacherRef(),
        activity as OnTeacherClickListener, activity as OnRatingClickListener)
    teachers_recycle_view.layoutManager = LinearLayoutManager(context)
    teachers_recycle_view.adapter = teachersAdapter
  }

  fun toggleEmptyTeachers(isEmpty: Boolean) {
    teachers_recycle_view.visibility = if (isEmpty) GONE else VISIBLE
    no_teachers_yet.visibility = if (isEmpty) VISIBLE else GONE
  }

  fun noInternetConnection() {
    hideDialog()
    teachers_recycle_view.visibility = GONE
    no_teachers_yet.visibility = GONE
    no_connection.visibility = VISIBLE
  }
}