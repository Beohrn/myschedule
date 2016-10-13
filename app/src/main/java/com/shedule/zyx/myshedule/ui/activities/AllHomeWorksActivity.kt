package com.shedule.zyx.myshedule.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.all_homeworks_activity
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.adapters.AllHomeWorkItemsAdapter
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity.Companion.ALL_HOMEWORK
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity.Companion.SCHEDULE_HOMEWORK_REQUEST
import kotlinx.android.synthetic.main.all_homeworks_activity.*
import org.jetbrains.anko.startActivityForResult

/**
 * Created by alexkowlew on 21.09.2016.
 */
class AllHomeWorksActivity: BaseActivity(), AllHomeWorkItemsAdapter.OnItemClick {

  var list = arrayListOf<Schedule>()
  lateinit var adapter: AllHomeWorkItemsAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(all_homeworks_activity)

    setSupportActionBar(ah_toolbar)
    supportActionBar?.let {
      with(it) {
        setHomeButtonEnabled(true)
        setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.task)
      }
    }

    with(ah_toolbar) {
      setTitleTextColor(Color.WHITE)
      setNavigationOnClickListener { finish() }
    }

    list_of_homeworks.layoutManager = LinearLayoutManager(applicationContext)
    list_of_homeworks.itemAnimator = DefaultItemAnimator()

    update()
    adapter = AllHomeWorkItemsAdapter(this, list)
    list_of_homeworks.adapter = adapter
    checkHomeworks()

  }

  fun checkHomeworks() {
    if (list.size != 0) {
      empty_homeworks.visibility = View.GONE
      list_of_homeworks.visibility = View.VISIBLE
    } else {
      empty_homeworks.visibility = View.VISIBLE
      list_of_homeworks.visibility = View.GONE
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    scheduleManager.editSchedule = null
  }

  override fun onClick(schedule: Schedule) {
    scheduleManager.editSchedule = schedule
    startActivityForResult<HomeWorkActivity>(SCHEDULE_HOMEWORK_REQUEST, ALL_HOMEWORK to true)
  }

  fun update() {
    list.addAll(scheduleManager.getAllHomework())
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_OK) {
      if (requestCode == SCHEDULE_HOMEWORK_REQUEST) {
        list.clear()
        update()
        checkHomeworks()
        adapter.notifyDataSetChanged()
      }
    }
  }
}