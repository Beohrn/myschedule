package com.shedule.zyx.myshedule.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.home_work_activity
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.adapters.HomeWorkItemsAdapter
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.models.HomeWork
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.ui.activities.CreateHomeWorkActivity.Companion.CREATE_HOMEWORK_REQUEST
import com.shedule.zyx.myshedule.ui.activities.CreateHomeWorkActivity.Companion.DATE_ON_TITLE
import com.shedule.zyx.myshedule.ui.activities.CreateHomeWorkActivity.Companion.EDIT_HOMEWORK_REQUEST
import com.shedule.zyx.myshedule.utils.Utils
import com.shedule.zyx.myshedule.widget.HomeWorkView
import kotlinx.android.synthetic.main.home_work_activity.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject

/**
 * Created by alexkowlew on 15.09.2016.
 */
class HomeWorkActivity : AppCompatActivity(),
    HomeWorkView.OnStatusChangeListener,
    HomeWorkItemsAdapter.OnHomeWorkClickListener,
    HomeWorkItemsAdapter.OnHomeWorkLongClickListener {

  @Inject
  lateinit var scheduleManager: ScheduleManager

  lateinit var adapter: HomeWorkItemsAdapter
  var homework = arrayListOf<HomeWork>()
  var schedule: Schedule? = null
  var date = ""
  var isAllHomework = false

  companion object {
    val SCHEDULE_HOMEWORK_REQUEST = 3923
    val HOMEWORK_BY_DATE = "homework_by_date"
    val ALL_HOMEWORK = "all_homework"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(home_work_activity)
    ScheduleApplication.getComponent().inject(this)

    setSupportActionBar(homework_toolbar)
    supportActionBar?.title = getString(R.string.homework_activity_title)
    homework_toolbar.setTitleTextColor(Color.WHITE)
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    homework_toolbar.setNavigationOnClickListener { activityFinish() }


    date = intent.getStringExtra(HOMEWORK_BY_DATE) ?: ""
    isAllHomework = intent.getBooleanExtra(ALL_HOMEWORK, false)

    schedule = scheduleManager.editSchedule
    schedule?.let {
      homework.clear()
      loadData(it)
    }

    if (isAllHomework)
      add_homework_fab.visibility = View.GONE

    adapter = HomeWorkItemsAdapter(this@HomeWorkActivity, homework, this)
    homework_list.layoutManager = LinearLayoutManager(applicationContext)
    homework_list.itemAnimator = DefaultItemAnimator()
    homework_list.adapter = adapter

    add_homework_fab.onClick {
      scheduleManager.editHomework = null
      startActivityForResult<CreateHomeWorkActivity>(CREATE_HOMEWORK_REQUEST, DATE_ON_TITLE to date)
    }
  }

  fun loadData(schedule: Schedule) {
    if (date.isNullOrBlank())
      homework.addAll(scheduleManager.getHomeWork(schedule))
    else
      homework.addAll(scheduleManager.getHomeWorkByDate(schedule, date))
  }

  override fun onBackPressed() {
    activityFinish()
  }

  fun activityFinish() {
    setResult(Activity.RESULT_OK)
    finish()
  }

  fun update(homeWork: HomeWork) {
    homework.add(homeWork)
  }

  override fun onDestroy() {
    super.onDestroy()
    scheduleManager.editSchedule = null
    scheduleManager.editHomework = null
  }

  override fun onChangeStatus(status: Boolean, taskName: String) {
    schedule?.homework?.map { it }?.find { it.taskName.equals(taskName) }?.status = status
  }

  override fun onHomeWorkClick(homeWork: HomeWork) {
    scheduleManager.editHomework = homeWork
    startActivityForResult<CreateHomeWorkActivity>(EDIT_HOMEWORK_REQUEST, DATE_ON_TITLE to homeWork.deadLine)
  }

  override fun onLongClick(homeWork: HomeWork) {
    alert("", getString(R.string.delete)) {
      positiveButton(getString(R.string.yes)) {
        schedule?.homework?.remove(homeWork)
        Utils.deleteHomeWorkDirectory(applicationContext, homeWork.taskName)
        dismiss()
        homework.clear()
        schedule?.let { loadData(it) }
        adapter.notifyDataSetChanged()
      }
      negativeButton(getString(R.string.no)) { dismiss() }
    }.show()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == CREATE_HOMEWORK_REQUEST) {
        val homework = HomeWork(data?.getStringExtra("name").toString(), false)
        homework.taskDescription = data?.getStringExtra("description").toString()
        homework.deadLine = date
        schedule?.homework?.add(homework)
        update(homework)
        adapter.notifyDataSetChanged()
      } else if (requestCode == EDIT_HOMEWORK_REQUEST) {
        adapter.notifyDataSetChanged()
      }
    }
  }

}