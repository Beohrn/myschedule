package com.shedule.zyx.myshedule.ui.activities

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.alexvasilkov.gestures.Settings
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.photo_activity
import com.shedule.zyx.myshedule.utils.Utils
import kotlinx.android.synthetic.main.photo_activity.*

/**
 * Created by alexkowlew on 20.09.2016.
 */
class PhotoActivity: BaseActivity() {

  companion object {
    val BITMAP = "bitmap_key"
    val BITMAP_NAME = "bitmap_name"
    val PHOTO_ACTIVITY_REQUEST = 8320
  }

  var path = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(photo_activity)

    setSupportActionBar(photo_toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    supportActionBar?.title = intent.getStringExtra(BITMAP_NAME)
    photo_toolbar.setTitleTextColor(Color.WHITE)

    photo_toolbar.setNavigationOnClickListener { finish() }

    path = intent.getStringExtra(BITMAP)
    cropping_image.setImageBitmap(BitmapFactory.decodeFile(path))
    cropping_image.controller.settings
        .setPanEnabled(true)
        .setZoomEnabled(true)
        .setDoubleTapEnabled(true)
        .setRotationEnabled(true)
        .setRestrictRotation(true)
        .setOverscrollDistance(0f, 0f)
        .setOverzoomFactor(2f)
        .setFillViewport(false)
        .setFitMethod(Settings.Fit.INSIDE).gravity = Gravity.CENTER
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.photo_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {R.id.delete_photo -> { Utils.deleteHomeWorkPhoto(path); setResult(Activity.RESULT_OK); finish() } }
    return super.onOptionsItemSelected(item)
  }

}