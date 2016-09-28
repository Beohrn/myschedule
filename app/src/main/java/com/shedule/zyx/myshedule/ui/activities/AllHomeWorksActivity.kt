package com.shedule.zyx.myshedule.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.all_homeworks_activity
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.adapters.AllHomeWorkItemsAdapter
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity.Companion.ALL_HOMEWORK
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity.Companion.SCHEDULE_HOMEWORK_REQUEST
import kotlinx.android.synthetic.main.all_homeworks_activity.*
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject

/**
 * Created by alexkowlew on 21.09.2016.
 */
class AllHomeWorksActivity : AppCompatActivity(), AllHomeWorkItemsAdapter.OnItemClick {

  @Inject
  lateinit var scheduleManager: ScheduleManager

  var list = arrayListOf<Schedule>()
  lateinit var adapter: AllHomeWorkItemsAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(all_homeworks_activity)
    ScheduleApplication.getComponent().inject(this)

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
        adapter.notifyDataSetChanged()
      }
    }
  }
}