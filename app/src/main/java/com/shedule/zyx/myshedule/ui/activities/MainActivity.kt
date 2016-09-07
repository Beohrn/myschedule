package com.shedule.zyx.myshedule.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.activity_navigation
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.adapters.ViewPagerAdapter
import com.shedule.zyx.myshedule.interfaces.ChangeStateFragmentListener
import com.shedule.zyx.myshedule.interfaces.DataChangeListener
import com.shedule.zyx.myshedule.managers.BluetoothManager
import com.shedule.zyx.myshedule.managers.DateManager
import com.shedule.zyx.myshedule.managers.ReceiveManager
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.ui.fragments.BluetoothDialog
import com.tbruyelle.rxpermissions.RxPermissions
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.app_bar_navigation.*
import kotlinx.android.synthetic.main.content_navigation.*
import kotlinx.android.synthetic.main.nav_header_navigation.*
import kotlinx.android.synthetic.main.nav_header_navigation.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.support.v4.onPageChangeListener
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ChangeStateFragmentListener, DatePickerDialog.OnDateSetListener {

  @Inject
  lateinit var dateManager: DateManager

  @Inject
  lateinit var bluetoothManager: BluetoothManager

  @Inject
  lateinit var receiveManager: ReceiveManager

  @Inject
  lateinit var scheduleManager: ScheduleManager

  val listenerList = arrayListOf<DataChangeListener>()
  private val CAMERA_REQUEST = 1888
  private val GALLERY_REQUEST = 2888


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(activity_navigation)
    ScheduleApplication.getComponent().inject(this)

    setSupportActionBar(main_toolbar)
    setupDataForViewPager(main_viewpager)
    main_tabs.setupWithViewPager(main_viewpager)

    nav_view?.setNavigationItemSelectedListener(this)
    ActionBarDrawerToggle(this, drawer_layout, main_toolbar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close).syncState()

    main_viewpager.currentItem = dateManager.getPositionByCalendar()
    main_toolbar.post { title = convertDateString(dateManager.getDayByPosition(main_viewpager.currentItem)) }

    main_viewpager.onPageChangeListener {
      onPageSelected {
        main_toolbar.title = convertDateString(dateManager.getDayByPosition(it))
      }
    }

    add_schedule_button.onClick {
      startActivityForResult<AddScheduleActivity>(5555, Pair("current_day_of_week", main_viewpager.currentItem + 2))
    }

    nav_view.inflateHeaderView(R.layout.nav_header_navigation).circleView.onClick {
      selector("Выберите, чтобы загрузить фото:", listOf("Камера", "Галерея")) { i ->
        when (i) {
          0 -> {
            startActivityForResult(Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST)
          }
          else -> {
            startActivityForResult(Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GALLERY_REQUEST)
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
    val adapter = ViewPagerAdapter(supportFragmentManager)
    viewPager.adapter = adapter
  }

  override fun onBackPressed() {
    if (drawer_layout?.isDrawerOpen(GravityCompat.START)!!) {
      drawer_layout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
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
    val now = dateManager.resetCalendar()
    val picker = DatePickerDialog.newInstance(
        this@MainActivity, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
        now.get(Calendar.DAY_OF_MONTH))
    picker.accentColor = Color.GRAY
    picker.show(fragmentManager, "")
  }

  override fun onNavigationItemSelected(item: MenuItem?): Boolean {
    //todo implement this
    when (item?.itemId) { R.id.nav_share -> {
      showDialog()
      bluetoothManager.schedule = scheduleManager.globalList
    }
      R.id.nav_manage -> startActivity<TeachersActivity>()
    }


    drawer_layout?.closeDrawer(GravityCompat.START)
    return true
  }

  override fun onStart() {
    super.onStart()
    checkPermissions()
    bluetoothInit()
    Log.i("TAG", "onStart")
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus)
      bluetoothInit()
  }

  override fun onDestroy() {
    super.onDestroy()
    bluetoothDestroy()
  }

  override fun onStop() {
    super.onStop()
    scheduleManager.saveSchedule()
    Log.i("TAG", "onStop")
  }

  private fun checkPermissions() {
    RxPermissions.getInstance(this)
        .request(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .subscribe()
  }

  private fun bluetoothInit() {
    if (bluetoothManager.bluetoothEnabled())
      if (!bluetoothManager.serviceAvailable()) {
        bluetoothManager.setupService()
        bluetoothManager.setReceiveListener(receiveManager)
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
      dialog.show(supportFragmentManager, "dialog")
    } else {
      bluetoothManager.autoConnect()
      bluetoothInit()
      showDialog()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == Activity.RESULT_OK) {

    } else if (requestCode == CAMERA_REQUEST) {
      val bitmap = data?.extras?.get("data") as? Bitmap
      bitmap?.let {
        circleView.setImageBitmap(it)
      }
    } else if (requestCode == GALLERY_REQUEST) {
      val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
      data?.let {
        val cursor = contentResolver.query(it.data, filePathColumn, null, null, null)
        cursor.moveToFirst()
        val picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
        cursor.close()
        circleView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
      }
    }
  }
}
