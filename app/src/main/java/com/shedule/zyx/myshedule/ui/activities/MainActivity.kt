package com.shedule.zyx.myshedule.ui.activities

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.activity_navigation
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.adapters.ScheduleItemsAdapter
import com.shedule.zyx.myshedule.adapters.ViewPagerAdapter
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.events.EventActivity
import com.shedule.zyx.myshedule.interfaces.ChangeStateFragmentListener
import com.shedule.zyx.myshedule.interfaces.DataChangeListener
import com.shedule.zyx.myshedule.managers.BluetoothManager
import com.shedule.zyx.myshedule.managers.DateManager
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.teachers.TeachersActivity
import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity.Companion.ADD_SCHEDULE_REQUEST
import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity.Companion.DAY_OF_WEEK_KEY
import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity.Companion.EDIT_SCHEDULE_REQUEST
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity.Companion.HOMEWORK_BY_DATE
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity.Companion.SCHEDULE_HOMEWORK_REQUEST
import com.shedule.zyx.myshedule.ui.fragments.BluetoothDialog
import com.shedule.zyx.myshedule.utils.Utils
import com.tbruyelle.rxpermissions.RxPermissions
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import de.cketti.mailto.EmailIntentBuilder.from
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.app_bar_navigation.*
import kotlinx.android.synthetic.main.content_navigation.*
import kotlinx.android.synthetic.main.nav_header_navigation.*
import kotlinx.android.synthetic.main.nav_header_navigation.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.onPageChangeListener
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ChangeStateFragmentListener, DatePickerDialog.OnDateSetListener, ScheduleItemsAdapter.ScheduleItemListener,
    BluetoothManager.OnScheduleReceiveListener {

  @Inject
  lateinit var dateManager: DateManager

  @Inject
  lateinit var bluetoothManager: BluetoothManager

  @Inject
  lateinit var scheduleManager: ScheduleManager

  @Inject
  lateinit var appPreference: AppPreference

  val listenerList = arrayListOf<DataChangeListener>()
  private val CAMERA_REQUEST = 1888
  private val GALLERY_REQUEST = 2888

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(activity_navigation)
    ScheduleApplication.getComponent().inject(this)
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
    nav.faculty_name.text = appPreference.getFacultyName()
    Utils.getAccountPhoto(applicationContext)?.let { nav.circleView.setImageBitmap(it) }
    Glide.with(this).load(R.drawable.univer_image)
        .bitmapTransform(BlurTransformation(this, 10))
        .into(nav.container_to_image)
    nav.circleView.onClick {
      selector(null, listOf(getString(R.string.camera),
          getString(R.string.gallery))) { i ->
        when (i) {
          0 -> {
            checkSinglePermission(Manifest.permission.CAMERA).subscribe({
              if (it) startActivityForResult(Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST)
              else toast(getString(R.string.no_permission_for_camera))
            }, {})
          }
          else -> {
            checkSinglePermission(Manifest.permission.READ_EXTERNAL_STORAGE).subscribe({
              if (it) startActivityForResult(Intent(Intent.ACTION_PICK,
                  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GALLERY_REQUEST)
              else toast(getString(R.string.no_permission_for_storage))
            }, {})
          }
        }
      }
    }
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
    if (drawer_layout?.isDrawerOpen(GravityCompat.START)!!) {
      drawer_layout.closeDrawer(GravityCompat.START)
    } else {
      finishAffinity()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.calendar_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.calendar -> openDataPicker()
    }
    return true
  }

  override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
    dateManager.updateCalendar(year, monthOfYear, dayOfMonth)
    main_viewpager.currentItem = dateManager.getPositionByCalendar(year, monthOfYear, dayOfMonth)
    main_toolbar.title = convertDateString(dateManager.getDayByPosition(main_viewpager.currentItem))
    listenerList.map { it.updateData() }
  }

  private fun openDataPicker() {
    val now = Calendar.getInstance()
    val picker = DatePickerDialog.newInstance(
        this@MainActivity, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
        now.get(Calendar.DAY_OF_MONTH))
    // for meizu
    picker.setAccentColor("#49a44c")
    picker.show(fragmentManager, "")
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.nav_share -> {
        checkSinglePermission(ACCESS_FINE_LOCATION).subscribe({
          if (it) {
            showDialog()
            bluetoothManager.schedule = scheduleManager.globalList
          } else toast(getString(R.string.no_permission_for_bluetooth))
        }, {})
      }
      R.id.open_events -> startActivity<EventActivity>()
      R.id.nav_teachers -> startActivity<TeachersActivity>()
      R.id.nav_tasks -> startActivity<AllHomeWorksActivity>()
      R.id.nav_write_to_us -> sendEmail()
      R.id.open_group -> openGroup()
      R.id.nav_delete_schedule -> deleteSchedule()
    }

    drawer_layout?.closeDrawer(GravityCompat.START)
    return true
  }

  fun openGroup() = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/club129716882")))

  fun deleteSchedule() {
    if (scheduleManager.globalList.size != 0) {
      alert(getString(R.string.delete), null) {
        positiveButton(getString(R.string.yes)) {
          scheduleManager.deleteSchedule()
          listenerList.map { it.updateData() }
        }
        negativeButton(getString(R.string.no))
      }.show()
    } else toast(getString(R.string.schedules_is_no))
  }

  fun sendEmail() =
      startActivity(from(this)
          .to(getString(R.string.email))
          .subject(getString(R.string.feedback))
          .build())

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
    scheduleManager.saveSchedule()
  }

  private fun checkSinglePermission(permission: String) = RxPermissions.getInstance(this)
      .request(permission)

  private fun checkAllPermissions() {
    RxPermissions.getInstance(this)
        .request(ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            CAMERA,
            READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe()
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

  fun showDialog() {
    if (bluetoothManager.bluetoothEnabled()) {
      val dialog = BluetoothDialog()
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
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        data?.let {
          val cursor = contentResolver.query(it.data, filePathColumn, null, null, null)
          cursor.moveToFirst()
          val picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
          cursor.close()
          val bitmap = BitmapFactory.decodeFile(picturePath)
          circleView.setImageBitmap(bitmap)
          Utils.saveAccountImage(applicationContext, bitmap)
        }
      }
    }
  }
}
