package com.shedule.zyx.myshedule.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.add_home_work_activity
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.adapters.ImageAdapter
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.models.HomeWork
import com.shedule.zyx.myshedule.ui.activities.PhotoActivity.Companion.BITMAP
import com.shedule.zyx.myshedule.ui.activities.PhotoActivity.Companion.BITMAP_NAME
import com.shedule.zyx.myshedule.ui.activities.PhotoActivity.Companion.PHOTO_ACTIVITY_REQUEST
import com.shedule.zyx.myshedule.utils.Utils
import kotlinx.android.synthetic.main.add_home_work_activity.*
import kotlinx.android.synthetic.main.create_home_work_screen.*
import org.jetbrains.anko.onItemClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.io.File
import javax.inject.Inject

/**
 * Created by alexkowlew on 15.09.2016.
 */
class CreateHomeWorkActivity : AppCompatActivity() {

  val photos = arrayListOf<File>()
  lateinit var photosAdapter: ImageAdapter

  @Inject
  lateinit var scheduleManager: ScheduleManager

  var homework: HomeWork? = null
  var isHomeworkEdit = false

  companion object {
    val CREATE_HOMEWORK_REQUEST = 1239
    val EDIT_HOMEWORK_REQUEST = 3928
    val DATE_ON_TITLE = "date_on_title"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(add_home_work_activity)
    ScheduleApplication.getComponent().inject(this)

    setSupportActionBar(add_homework_toolbar)
    supportActionBar?.let {
      with(it) {
        setHomeButtonEnabled(true)
        setDisplayHomeAsUpEnabled(true)
        title = Utils.convertDateString(intent.getStringExtra(DATE_ON_TITLE) ?: "")
      }
    }
    with(add_homework_toolbar) {
      setTitleTextColor(Color.WHITE)
      setNavigationOnClickListener { finish() }
    }
    photosAdapter = ImageAdapter(this, photos)
    photo_grid.adapter = photosAdapter

    homework = scheduleManager.editHomework

    homework?.let {
      isHomeworkEdit = true

      home_work_name.setText(it.taskName)
      home_work_description.setText(it.taskDescription)
      Utils.getHomeWorkImagePath(this, home_work_name.getText())?.let {
        updatePhotos(it)
        photosAdapter.notifyDataSetChanged()
      }
    }

    photo_grid.onItemClick { adapterView, view, i, l ->
      startActivityForResult<PhotoActivity>(PHOTO_ACTIVITY_REQUEST, BITMAP to photos[i].path, BITMAP_NAME to photos[i].name)
    }
  }

  fun updatePhotos(image: Array<out File>) {
    photos.clear()
    photos.addAll(image.map { it })
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.add_home_work_screen_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.add_homework_photo -> {
        if (!home_work_name.getText().isNullOrBlank()) {
          selector(null, listOf(getString(R.string.camera),
              getString(R.string.gallery))) { i ->
            when (i) {
              0 -> {
                val intent = Intent(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                val uri = Uri.fromFile(Utils.getHomeWorkFile(this, home_work_name.getText()))
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, 9393)
              }
              else -> {
                startActivityForResult(Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 3288)
              }
            }
          }
        } else toast(getString(R.string.enter_name))
      }

      R.id.add_homework -> {

        if (!isHomeworkEdit) {
          val intent = Intent()
          intent.putExtra("name", home_work_name.getText())
          intent.putExtra("description", home_work_description.getText())
          setResult(Activity.RESULT_OK, intent)
        } else {
          homework?.taskName = home_work_name.getText()
          homework?.taskDescription = home_work_description.getText()
          setResult(Activity.RESULT_OK)
        }
        finish()
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == 9393) {

        updatePhotos(Utils.getHomeWorkImagePath(this, home_work_name.getText()))
        photosAdapter.notifyDataSetChanged()
      } else if (requestCode == 3288) {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        data?.let {
          val cursor = contentResolver.query(it.data, filePathColumn, null, null, null)
          cursor.moveToFirst()
          val picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
          cursor.close()
          val bitmap = BitmapFactory.decodeFile(picturePath)
          updateHomeWorkPhotos(bitmap)
        }
      } else if (requestCode == PHOTO_ACTIVITY_REQUEST) {
        updatePhotos(Utils.getHomeWorkImagePath(this, home_work_name.getText()))
        photosAdapter.notifyDataSetChanged()
      }
    }
  }

  private fun updateHomeWorkPhotos(bitmap: Bitmap) {
    Utils.saveHomeWorkImage(applicationContext, bitmap, home_work_name.getText())
    updatePhotos(Utils.getHomeWorkImagePath(applicationContext, home_work_name.getText()))
    photosAdapter.notifyDataSetChanged()
  }
}