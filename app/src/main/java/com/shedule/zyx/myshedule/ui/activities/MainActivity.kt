package com.shedule.zyx.myshedule.ui.activities

import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.Intent.ACTION_VIEW
import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeFile
import android.net.Uri.parse
import android.os.Bundle
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.Images.Media.DATA
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat.START
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.google.firebase.crash.FirebaseCrash.report
import com.google.gson.Gson
import com.shedule.zyx.myshedule.BuildConfig.DEBOUG_ENABLED
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.drawable.notification
import com.shedule.zyx.myshedule.R.id.*
import com.shedule.zyx.myshedule.R.layout.activity_navigation
import com.shedule.zyx.myshedule.R.layout.nav_header_navigation
import com.shedule.zyx.myshedule.R.string.*
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.adapters.ScheduleItemsAdapter
import com.shedule.zyx.myshedule.adapters.ViewPagerAdapter
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.interfaces.ChangeStateFragmentListener
import com.shedule.zyx.myshedule.interfaces.DataChangeListener
import com.shedule.zyx.myshedule.managers.BluetoothManager
import com.shedule.zyx.myshedule.managers.DateManager
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.teachers.TeachersActivity
import com.shedule.zyx.myshedule.tutorial.TutorialActivity
import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity.Companion.ADD_SCHEDULE_REQUEST
import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity.Companion.DAY_OF_WEEK_KEY
import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity.Companion.EDIT_SCHEDULE_REQUEST
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity.Companion.HOMEWORK_BY_DATE
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity.Companion.SCHEDULE_HOMEWORK_REQUEST
import com.shedule.zyx.myshedule.ui.fragments.BluetoothDialog
import com.shedule.zyx.myshedule.utils.Utils
import com.shedule.zyx.myshedule.utils.Utils.Companion.isOnline
import com.shedule.zyx.myshedule.utils.Utils.Companion.saveAccountImage
import com.tbruyelle.rxpermissions.RxPermissions.getInstance
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance
import de.cketti.mailto.EmailIntentBuilder.from
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.app_bar_navigation.*
import kotlinx.android.synthetic.main.content_navigation.*
import kotlinx.android.synthetic.main.nav_header_navigation.*
import kotlinx.android.synthetic.main.nav_header_navigation.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.onPageChangeListener
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import java.util.Calendar.*
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

  @Inject
  lateinit var firebaseWraper: FirebaseWrapper

  @Inject
  lateinit var gson: Gson
  lateinit var update: MenuItem
  var changesCount = 0

  var subscription: Subscription? = null

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

    val nav = nav_view.inflateHeaderView(nav_header_navigation)
    nav.faculty_name.text = appPreference.getFacultyName()
    Utils.getAccountPhoto(applicationContext)?.let { nav.circleView.setImageBitmap(it) }
    Glide.with(this).load(R.drawable.univer_image)
        .bitmapTransform(BlurTransformation(this, 10))
        .into(nav.container_to_image)
    nav.circleView.onClick {
      selector(null, listOf(getString(camera),
          getString(gallery))) { i ->
        when (i) {
          0 -> {
            checkSinglePermission(CAMERA).subscribe({
              if (it) startActivityForResult(Intent(ACTION_IMAGE_CAPTURE), CAMERA_REQUEST)
              else toast(getString(no_permission_for_camera))
            }, {})
          }
          else -> {
            checkSinglePermission(READ_EXTERNAL_STORAGE).subscribe({
              if (it) startActivityForResult(Intent(ACTION_PICK,
                  EXTERNAL_CONTENT_URI), GALLERY_REQUEST)
              else toast(getString(no_permission_for_storage))
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
        } else toast(getString(connection_is_failed))
      }
    }
    return true
  }

  private fun pushSchedule() {
    subscription?.unsubscribe()
    val dialog = indeterminateProgressDialog(getString(load))
    var changes = appPreference.getChangesCount()
    changes++
    subscription = firebaseWraper.pushSchedule(scheduleManager.globalList, changes)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ done ->
          if (done) {
            dialog.dismiss()
            appPreference.saveChangesCount(changes)
            toast(getString(schedule_was_sent))
          }
        }, {
          if (DEBOUG_ENABLED) report(it)
          dialog.dismiss()
        })
  }

  private fun pullSchedule() {
    subscription?.unsubscribe()
    val dialog = indeterminateProgressDialog(getString(load))
    subscription = firebaseWraper.getSchedule()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ schedule ->
          schedule?.let {
            dialog.dismiss()
            scheduleManager.globalList.clear()
            scheduleManager.globalList.addAll(it)
            if (changesCount != 0) appPreference.saveChangesCount(changesCount)
            else getChangesCount()
            listenerList.map { it.updateData() }
            update.icon = resources.getDrawable(R.drawable.alarm)
            toast(getString(schedule_was_updated))
          } ?: toast(getString(group_has_not_been_created))
        }, {
          if (DEBOUG_ENABLED) report(it)
          dialog.dismiss()
          toast(getString(no_data))
        })
  }

  private fun getChangesCount() {
    subscription = firebaseWraper.getChangesCount()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ count ->
          count?.let {
            if (changesCount == 0) {
              if (it > appPreference.getChangesCount()) {
                changesCount = it
                update.icon = resources.getDrawable(notification)
              }
            } else appPreference.saveChangesCount(it)
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
      nav_share -> {
        if (appPreference.getAdminRight()) {
          selector(null, listOf(getString(via_bluetooth), getString(via_server))) {
            when (it) {
              0 -> startBluetooth()
              1 -> {
                if (isOnline(applicationContext)) {
                  if (scheduleManager.globalList.size != 0) pushSchedule()
                  else toast(getString(schedules_is_no))
                } else toast(getString(connection_is_failed))
              }
            }
          }
        } else startBluetooth()
      }
      nav_teachers -> startActivity<TeachersActivity>()
      nav_tasks -> startActivity<AllHomeWorksActivity>()
      nav_write_to_us -> sendEmail()
      R.id.open_group -> openGroup()
      nav_delete_schedule -> deleteSchedule()
      nav_log_out -> {
        if (isOnline(applicationContext)) logOut()
        else toast(getString(connection_is_failed))
      }
      nav_settings -> startActivity<SettingsActivity>()
    }

    drawer_layout?.closeDrawer(START)
    return true
  }

  fun logOut() {
    subscription?.unsubscribe()
    val dialog = indeterminateProgressDialog(getString(log_out))
    removeAdmin()
    firebaseWraper.logOut()
        .doOnTerminate { dialog.dismiss() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          scheduleManager.globalList.clear()
          appPreference.saveChangesCount(0)
          appPreference.saveUniverName("")
          appPreference.saveFacultyName("")
          startActivity<TutorialActivity>()
        }, {
          if (DEBOUG_ENABLED) report(it)
          toast(getString(log_out_error))
        })
  }

  fun removeAdmin() {
    if (appPreference.getAdminRight())
      firebaseWraper.removeAdmin()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            if (it) {
              appPreference.saveAdminKey("")
              appPreference.saveAdminRights(false)
            }
          }, { if (DEBOUG_ENABLED) report(it) })
  }

  fun startBluetooth() {
    checkSinglePermission(ACCESS_FINE_LOCATION).subscribe({
      if (it) {
        showDialog()
        bluetoothManager.schedule = scheduleManager.globalList
      } else toast(getString(no_permission_for_bluetooth))
    }, {})
  }

  fun openGroup() = startActivity(Intent(ACTION_VIEW, parse("https://vk.com/club129716882")))

  fun deleteSchedule() {
    if (scheduleManager.globalList.size != 0) {
      alert(getString(delete), null) {
        positiveButton(getString(yes)) {
          scheduleManager.deleteSchedule()
          listenerList.map { it.updateData() }
        }
        negativeButton(getString(no))
      }.show()
    } else toast(getString(schedules_is_no))
  }

  fun sendEmail() =
      startActivity(from(this)
          .to(getString(R.string.email))
          .subject(getString(feedback))
          .build())

  override fun onScheduleReceived(schedules: ArrayList<Schedule>) {
    alert("", getString(receive_single_schedule)) {
      positiveButton(getString(yes)) {
        if (schedules.size > 1) {
          dismiss()
          selector(null, listOf(getString(merge_schedule), getString(override_schedule))) {
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
      negativeButton(getString(no)) { dismiss() }
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
    selector(null, listOf(getString(home_task),
        getString(send),
        getString(edit),
        getString(delete_lesson))) { position ->
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
          alert(getString(delete), null) {
            positiveButton(getString(yes)) {
              scheduleManager.removeSchedule(schedule)
              listenerList.map { it.updateData() }
              add_schedule_button.show()
            }
            negativeButton(getString(no))
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
          saveAccountImage(applicationContext, it)
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
      }
    }
  }
}
