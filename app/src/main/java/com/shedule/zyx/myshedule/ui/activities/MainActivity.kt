package com.shedule.zyx.myshedule.ui.activities

import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeFile
import android.os.Bundle
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.Images.Media.DATA
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat.START
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.google.firebase.crash.FirebaseCrash.report
import com.shedule.zyx.myshedule.BuildConfig.DEBOUG_ENABLED
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.activity_navigation
import com.shedule.zyx.myshedule.adapters.ScheduleItemsAdapter
import com.shedule.zyx.myshedule.adapters.ViewPagerAdapter
import com.shedule.zyx.myshedule.events.EventActivity
import com.shedule.zyx.myshedule.interfaces.ChangeStateFragmentListener
import com.shedule.zyx.myshedule.interfaces.DataChangeListener
import com.shedule.zyx.myshedule.managers.BluetoothManager
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.teachers.TeachersActivity
import com.shedule.zyx.myshedule.tutorial.TutorialActivity
import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity.Companion.ADD_SCHEDULE_REQUEST
import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity.Companion.DAY_OF_WEEK_KEY
import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity.Companion.EDIT_SCHEDULE_REQUEST
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity.Companion.HOMEWORK_BY_DATE
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity.Companion.SCHEDULE_HOMEWORK_REQUEST
import com.shedule.zyx.myshedule.ui.activities.SettingsActivity.Companion.BECOME_MANAGER_KEY
import com.shedule.zyx.myshedule.ui.activities.SettingsActivity.Companion.MANAGER_REQUEST
import com.shedule.zyx.myshedule.ui.fragments.BluetoothDialog
import com.shedule.zyx.myshedule.utils.Utils
import com.shedule.zyx.myshedule.utils.Utils.Companion.isOnline
import com.shedule.zyx.myshedule.utils.Utils.Companion.saveAccountImage
import com.shedule.zyx.myshedule.utils.toMainThread
import com.tbruyelle.rxpermissions.RxPermissions.getInstance
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.app_bar_navigation.*
import kotlinx.android.synthetic.main.content_navigation.*
import kotlinx.android.synthetic.main.nav_header_navigation.*
import kotlinx.android.synthetic.main.nav_header_navigation.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.onPageChangeListener
import java.util.*
import java.util.Calendar.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    ChangeStateFragmentListener, DatePickerDialog.OnDateSetListener, ScheduleItemsAdapter.ScheduleItemListener,
    BluetoothManager.OnScheduleReceiveListener {

  lateinit var update: MenuItem
  var changesCount = 0

  val listenerList = arrayListOf<DataChangeListener>()
  private val CAMERA_REQUEST = 1888
  private val GALLERY_REQUEST = 2888
  private var isUpdateClicked = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(activity_navigation)
    subscription?.unsubscribe()
    checkAllPermissions()
    setSupportActionBar(main_toolbar)
    setupDataForViewPager(main_viewpager)
    main_tabs.setupWithViewPager(main_viewpager)
    nav_view?.setNavigationItemSelectedListener(this)
    ActionBarDrawerToggle(this, drawer_layout, main_toolbar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close).syncState()

    main_viewpager.currentItem = dateManager.getPositionByCalendar()
    main_toolbar.post { title = convertDateString(dateManager.getDayByPosition(main_viewpager.currentItem)) }

    main_viewpager.onPageChangeListener {
      onPageSelected { main_toolbar.title = convertDateString(dateManager.getDayByPosition(it)) }
    }

    add_schedule_button.onClick {
      startActivityForResult<AddScheduleActivity>(ADD_SCHEDULE_REQUEST,
          Pair("current_day_of_week", main_viewpager.currentItem + 2))
    }

    val nav = nav_view.inflateHeaderView(R.layout.nav_header_navigation)
    nav.faculty_name.text = prefs.getFacultyName()
    Utils.getAccountPhoto(applicationContext)?.let { nav.circleView.setImageBitmap(it) }
    Glide.with(this).load(R.drawable.univer_image)
        .bitmapTransform(BlurTransformation(this, 10))
        .into(nav.container_to_image)
    nav.circleView.onClick {
      selector(null, listOf(getString(R.string.camera),
          getString(R.string.gallery))) { i ->
        when (i) {
          0 -> {
            checkSinglePermission(CAMERA).subscribe({
              if (it) startActivityForResult(Intent(ACTION_IMAGE_CAPTURE), CAMERA_REQUEST)
              else toast(getString(R.string.no_permission_for_camera))
            }, {})
          }
          else -> {
            checkSinglePermission(READ_EXTERNAL_STORAGE).subscribe({
              if (it) startActivityForResult(Intent(ACTION_PICK,
                  EXTERNAL_CONTENT_URI), GALLERY_REQUEST)
              else toast(getString(R.string.no_permission_for_storage))
            }, {})
          }
        }
      }
    }
  }

  override fun onPause() {
    subscription?.unsubscribe()
    super.onPause()
  }

  private fun convertDateString(dateString: String): String {
    val day = dateString.split("-")[0]
    val month = dateManager.getMonthName(dateString.split("-")[1].toInt())
    return "$day $month"
  }

  override fun addListener(listener: DataChangeListener) {
    listenerList.add(listener)
  }

  override fun removeListener(listener: DataChangeListener) {
    listenerList.remove(listener)
  }

  private fun setupDataForViewPager(viewPager: ViewPager) {
    val adapter = ViewPagerAdapter(this, supportFragmentManager)
    viewPager.adapter = adapter
  }

  override fun onBackPressed() {
    if (drawer_layout?.isDrawerOpen(START)!!) {
      drawer_layout.closeDrawer(START)
    } else {
      finishAffinity()
    }
  }

  override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
    update = menu?.findItem(R.id.update)!!
    if (isOnline(applicationContext))
      getChangesCount()
    return super.onPrepareOptionsMenu(menu)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.calendar_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.calendar -> openDataPicker()
      R.id.update -> {
        if (isOnline(applicationContext)) {
          pullSchedule()
        } else toast(getString(R.string.connection_is_failed))
      }
    }
    return true
  }

  private fun pushSchedule() {
    isUpdateClicked = true
    val dialog = indeterminateProgressDialog(getString(R.string.load))
    var changes = prefs.getChangesCount()
    changes++
    firebaseWrapper.pushSchedule(scheduleManager.globalList, changes)
        .toMainThread()
        .subscribe({ done ->
          if (done) {
            dialog.dismiss()
            if (prefs.getAdminRight()) {
              prefs.saveChangesCount(changes)
              toast(getString(R.string.schedule_was_sent))
            }
          }
        }, {
          if (DEBOUG_ENABLED) report(it)
          dialog.dismiss()
        })
  }

  private fun pullSchedule() {
    isUpdateClicked = false
    val dialog = indeterminateProgressDialog(getString(R.string.load))
    subscription = firebaseWrapper.getSchedule()
        .toMainThread()
        .subscribe({ schedule ->
          schedule?.let {
            dialog.dismiss()
            if (!isUpdateClicked) {
              selector(null, listOf(getString(R.string.merge_schedule), getString(R.string.override_schedule))) { position ->
                when (position) {
                  0 -> {
                    scheduleManager.globalList.addAll(it)
                    updateSchedule()
                  }
                  1 -> {
                    scheduleManager.globalList.clear()
                    scheduleManager.globalList.addAll(it)
                    updateSchedule()
                  }
                }
              }
            }

          } ?: toast(getString(R.string.group_has_not_been_created))
        }, {
          if (DEBOUG_ENABLED) report(it)
          dialog.dismiss()
          toast(getString(R.string.schedules_is_no))
        })
  }

  private fun updateSchedule() {
    if (changesCount != 0) prefs.saveChangesCount(changesCount)
    else getChangesCount()
    listenerList.map { it.updateData() }
    update.icon = resources.getDrawable(R.drawable.alarm)
    toast(getString(R.string.schedule_was_updated))
    subscription?.unsubscribe()
    isUpdateClicked = true
  }

  private fun getChangesCount() {
    subscription = firebaseWrapper.getChangesCount()
        .toMainThread()
        .subscribe({ count ->
          count?.let {
            if (changesCount == 0) {
              if (it > prefs.getChangesCount()) {
                changesCount = it
                if (!prefs.getAdminRight())
                  update.icon = resources.getDrawable(R.drawable.notification)
              }
            } else prefs.saveChangesCount(it)
          }
        }, { if (DEBOUG_ENABLED) report(it) })
  }

  override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
    dateManager.updateCalendar(year, monthOfYear, dayOfMonth)
    main_viewpager.currentItem = dateManager.getPositionByCalendar(year, monthOfYear, dayOfMonth)
    main_toolbar.title = convertDateString(dateManager.getDayByPosition(main_viewpager.currentItem))
    listenerList.map { it.updateData() }
  }

  private fun openDataPicker() {
    val now = getInstance()
    val picker = newInstance(
        this@MainActivity, now.get(YEAR), now.get(MONTH),
        now.get(DAY_OF_MONTH))
    // for meizu
    picker.setAccentColor("#49a44c")
    picker.show(fragmentManager, "")
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.nav_share_with_group -> {
        if (scheduleManager.globalList.size != 0) {
          if (isOnline(applicationContext)) {
            if (prefs.getAdminRight())
              pushSchedule()
            else startActivityForResult<SettingsActivity>(MANAGER_REQUEST,
                BECOME_MANAGER_KEY to true)
          } else toast(getString(R.string.connection_is_failed))
        } else toast(getString(R.string.schedules_is_no))
      }
      R.id.nav_share_via_bluetooth -> {
        if (scheduleManager.globalList.size != 0) startBluetooth()
        else toast(getString(R.string.schedules_is_no))
      }
      R.id.nav_teachers -> startActivity<TeachersActivity>()
      R.id.nav_tasks -> startActivity<AllHomeWorksActivity>()
      R.id.nav_delete_schedule -> deleteSchedule()
      R.id.nav_log_out -> {
        if (isOnline(applicationContext)) logOut()
        else toast(getString(R.string.connection_is_failed))
      }
      R.id.nav_settings -> startActivity<SettingsActivity>()
      R.id.open_events -> startActivity<EventActivity>()
    }

    drawer_layout?.closeDrawer(START)
    return true
  }

  fun logOut() {
    subscription?.unsubscribe()
    showProgressDialog(getString(R.string.exit))
    firebaseWrapper.logOut()
        .doOnTerminate { hideProgressDialog() }
        .toMainThread()
        .subscribe({
          prefs.saveChangesCount(0)
          scheduleManager.saveSchedule()
          prefs.saveUniverName("")
          prefs.saveFacultyName("")
          prefs.saveGroupName("")
          prefs.saveAdminKey("")
          prefs.saveAdminRights(false)
          scheduleManager.globalList.clear()
          startActivity<TutorialActivity>()
        }, {
          if (DEBOUG_ENABLED) report(it)
          toast(getString(R.string.log_out_error))
        })
  }

  fun startBluetooth() {
    checkSinglePermission(ACCESS_FINE_LOCATION).subscribe({
      if (it) {
        showDialog()
        bluetoothManager.schedule = scheduleManager.globalList
      } else toast(getString(R.string.no_permission_for_bluetooth))
    }, {})
  }

  fun deleteSchedule() {
    if (scheduleManager.globalList.size != 0) {
      alert(getString(R.string.delete), null) {
        positiveButton(getString(R.string.yes)) {
          scheduleManager.deleteSchedule()
          scheduleManager.saveSchedule()
          listenerList.map { it.updateData() }
        }
        negativeButton(getString(R.string.no))
      }.show()
    } else toast(getString(R.string.schedules_is_no))
  }

  override fun onScheduleReceived(schedules: ArrayList<Schedule>) {
    alert("", getString(R.string.receive_single_schedule)) {
      positiveButton(getString(R.string.yes)) {
        if (schedules.size > 1) {
          dismiss()
          selector(null, listOf(getString(R.string.merge_schedule), getString(R.string.override_schedule))) {
            when (it) {
              0 -> {
                scheduleManager.globalList.addAll(schedules.map { it })
                listenerList.map { it.updateData() }
              }
              1 -> {
                scheduleManager.globalList.clear()
                scheduleManager.globalList.addAll(schedules.map { it })
                listenerList.map { it.updateData() }
              }
            }
          }
        } else {
          scheduleManager.globalList.addAll(schedules.map { it })
          listenerList.map { it.updateData() }
          dismiss()
        }
      }
      negativeButton(getString(R.string.no)) { dismiss() }
    }.show()
  }

  override fun onStart() {
    super.onStart()
    bluetoothInit()
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus) {
      bluetoothInit()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    bluetoothDestroy()
  }

  override fun onStop() {
    super.onStop()
    saveSchedule()
  }

  private fun checkSinglePermission(permission: String) = getInstance(this)
      .request(permission)

  private fun checkAllPermissions() {
    getInstance(this)
        .request(ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            CAMERA,
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE).subscribe()
  }

  private fun bluetoothInit() {
    if (bluetoothManager.bluetoothEnabled())
      if (!bluetoothManager.serviceAvailable()) {
        bluetoothManager.setupService()
        bluetoothManager.setScheduleReceiveListener(this)
      }
  }

  private fun bluetoothDestroy() {
    if (bluetoothManager.bluetoothEnabled())
      if (bluetoothManager.serviceAvailable()) {
        bluetoothManager.stopService()
      }
  }

  tailrec fun showDialog() {
    val dialog = BluetoothDialog()
    if (bluetoothManager.bluetoothEnabled()) {
      dialog.show(supportFragmentManager, "")
    } else {
      bluetoothManager.autoConnect()
      bluetoothInit()
      showDialog()
    }
  }

  override fun scheduleItemClick(schedule: Schedule) {
    selector(null, listOf(getString(R.string.home_task),
        getString(R.string.send),
        getString(R.string.edit),
        getString(R.string.delete_lesson))) { position ->
      when (position) {
        0 -> {
          scheduleManager.editSchedule = schedule
          startActivityForResult<HomeWorkActivity>(SCHEDULE_HOMEWORK_REQUEST,
              HOMEWORK_BY_DATE to dateManager.getDayByPosition(main_viewpager.currentItem))
        }
        1 -> {
          showDialog(); bluetoothManager.schedule = arrayListOf(schedule)
        }
        2 -> {
          scheduleManager.editSchedule = schedule
          startActivityForResult<AddScheduleActivity>(EDIT_SCHEDULE_REQUEST,
              Pair(DAY_OF_WEEK_KEY, main_viewpager.currentItem + 2))
        }
        3 -> {
          alert(getString(R.string.delete), null) {
            positiveButton(getString(R.string.yes)) {
              scheduleManager.removeSchedule(schedule)
              scheduleManager.saveSchedule()
              listenerList.map { it.updateData() }
              add_schedule_button.show()
            }
            negativeButton(getString(R.string.no))
          }.show()
        }
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == ADD_SCHEDULE_REQUEST) {
        listenerList.map { it.updateData() }
      } else if (requestCode == EDIT_SCHEDULE_REQUEST) {
        listenerList.map { it.updateData() }
      } else if (requestCode == CAMERA_REQUEST) {
        val bitmap = data?.extras?.get("data") as? Bitmap
        bitmap?.let {
          circleView.setImageBitmap(it)
          Utils.saveAccountImage(applicationContext, it)
        }
      } else if (requestCode == GALLERY_REQUEST) {
        val filePathColumn = arrayOf(DATA)
        data?.let {
          val cursor = contentResolver.query(it.data, filePathColumn, null, null, null)
          cursor.moveToFirst()
          val picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
          cursor.close()
          val bitmap = decodeFile(picturePath)
          circleView.setImageBitmap(bitmap)
          saveAccountImage(applicationContext, bitmap)
        }
      } else if (requestCode == MANAGER_REQUEST) pushSchedule()
    }
  }
}
