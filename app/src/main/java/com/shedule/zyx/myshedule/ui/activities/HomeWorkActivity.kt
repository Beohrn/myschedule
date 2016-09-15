package com.shedule.zyx.myshedule.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.home_work_activity
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.adapters.HomeWorkItemsAdapter
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.models.HomeWork
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.widget.HomeWorkView
import kotlinx.android.synthetic.main.create_home_work_dialog.view.*
import kotlinx.android.synthetic.main.home_work_activity.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.include
import org.jetbrains.anko.onClick
import javax.inject.Inject

/**
 * Created by alexkowlew on 15.09.2016.
 */
class HomeWorkActivity : AppCompatActivity(), HomeWorkView.OnStatusChangeListener, HomeWorkItemsAdapter.OnHomeWorkClickListener {

  @Inject
  lateinit var scheduleManager: ScheduleManager

  var homework = arrayListOf<HomeWork>()
  lateinit var adapter: HomeWorkItemsAdapter
  var schedule: Schedule? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(home_work_activity)
    ScheduleApplication.getComponent().inject(this)

    setSupportActionBar(homework_toolbar)
    supportActionBar?.title = getString(R.string.homework_activity_title)
    homework_toolbar.setTitleTextColor(Color.WHITE)
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    homework_toolbar.setNavigationOnClickListener { finish() }

    schedule = scheduleManager.editSchedule
    update()
    adapter = HomeWorkItemsAdapter(this@HomeWorkActivity, homework, this)
    homework_list.layoutManager = LinearLayoutManager(applicationContext)
    homework_list.itemAnimator = DefaultItemAnimator()
    homework_list.adapter = adapter
  }

  fun update() {
    homework.clear()
    homework.addAll(scheduleManager.getHomeWorkBySchedule(schedule!!))
  }

  override fun onDestroy() {
    super.onDestroy()
    scheduleManager.editSchedule = null
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.add_homework -> {
        alert {
          customView {
            include<View>(R.layout.create_home_work_dialog) {
              find<Button>(R.id.hw_ok).onClick {
                schedule?.homework?.add(HomeWork(create_task_name_field.text.toString(), false))
                update()
                adapter.notifyDataSetChanged()
                dismiss()
              }
              find<Button>(R.id.hw_cancel_dialog).onClick { dismiss() }
            }
          }
        }.show()
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.homework_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onChangeStatus(status: Boolean, taskName: String) {
    schedule?.homework?.map { it }?.find { it.taskName.equals(taskName) }?.status = status
  }

  override fun onHomeWorkClick(homeWork: HomeWork) {
    schedule?.homework?.remove(homeWork)
    update()
    adapter.notifyDataSetChanged()
  }

}