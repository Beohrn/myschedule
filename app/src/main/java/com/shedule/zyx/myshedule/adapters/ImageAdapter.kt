package com.shedule.zyx.myshedule.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.image_row.view.*
import java.io.File
import java.util.*

/**
 * Created by alexkowlew on 19.09.2016.
 */
class ImageAdapter(val context: Context, val bitmaps: ArrayList<File>) : BaseAdapter() {

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    val image = ImageView(context)
    val view = (context as Activity).layoutInflater.inflate(R.layout.image_row, parent, false)
    convertView?.let {
      image.layoutParams = AbsListView.LayoutParams(500, 700)

    }
    view.image_row.setImageBitmap(getBitmap(position))
    return view
  }

  // for save memory
  private fun getBitmap(position: Int): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(bitmaps[position].path, options)
    val width = options.outWidth
    val height = options.outHeight
    val scaleFactor = Math.min(width / 120, height / 120)
    options.inJustDecodeBounds = false
    options.inSampleSize = scaleFactor

    return BitmapFactory.decodeFile(bitmaps[position].path, options)
  }

  override fun getItem(position: Int) = bitmaps[position]

  override fun getItemId(position: Int) = 0.toLong()

  override fun getCount() = bitmaps.size

}