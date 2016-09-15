package com.shedule.zyx.myshedule.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.amazonaws.util.Base64
import com.google.gson.Gson
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.models.Category
import com.shedule.zyx.myshedule.models.Date
import com.shedule.zyx.myshedule.models.Schedule
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Created by alexkowlew on 31.08.2016.
 */
class Utils {

  companion object {
    fun getColorByCategory(context: Context, category: Category) = when (category) {
      Category.EXAM -> context.resources.getColor(R.color.mark_red)
      Category.STANDINGS -> context.resources.getColor(R.color.mark_yellow)
      Category.COURSE_WORK -> context.resources.getColor(R.color.mark_orange)
      Category.HOME_EXAM -> context.resources.getColor(R.color.dark_cyan)
    }

    fun toJson(list: ArrayList<Schedule>) = Gson().toJson(list)

    fun getColorByAssessment(context: Context, assessment: String) = when (assessment) {
      "E" -> context.resources.getColor(R.color.assess_E)
      "D" -> context.resources.getColor(R.color.assess_D)
      "C" -> context.resources.getColor(R.color.assess_C)
      "B" -> context.resources.getColor(R.color.assess_B)
      "A" -> context.resources.getColor(R.color.assess_A)
      else -> 0
    }

    fun getKeyByName(name: String) = String(Base64.encode(name.toByteArray()))

    fun getNameByKey(key: String) = String(Base64.decode(key))

    fun decoder(text: String) = text.replace("і", "и")

    fun getAverageAssessment(assessments: HashMap<String, Int>): Double {
      var result = 0.0
      assessments.map { result += it.value }
      return result / assessments.size
    }

    fun getLetterByAverageAssessment(assessment: Double): String {
      if (assessment >= 95.0 && assessment <= 100.0)
        return "A"
      else if (assessment >= 85.0 && assessment <= 94.99)
        return "B"
      else if (assessment >= 75.0 && assessment <= 84.99)
        return "C"
      else if (assessment >= 65.0 && assessment <= 74.99)
        return "D"
      else
        return "E"
    }

    fun saveImage(context: Context, bitmap: Bitmap) {
      val mediaFile = getMediaFile(context)
      mediaFile?.let {
        val fos = FileOutputStream(it)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
      } ?: return
    }

    fun getMediaFile(context: Context): File? {
      val mediaStorageDir = File("${Environment.getExternalStorageDirectory()}/Android/data/ ${context.packageName}/Files")

      if (!mediaStorageDir.exists())
        if (mediaStorageDir.mkdirs())
          return null

      val imageName = "Sch_account_photo.jpg"
      return File("${mediaStorageDir.path}${File.separator}$imageName")
    }

    fun getBitmap(context: Context): Bitmap? {
      val image = File("${Environment.getExternalStorageDirectory()}/Android/data/ ${context.packageName}/Files/Sch_account_photo.jpg")
      return BitmapFactory.decodeFile(image.toString())
    }

    fun getNormalizedDate(date: Date) =
        if (date.dayOfMonth < 10 && date.monthOfYear < 9)
          "0${date.dayOfMonth}.0${date.monthOfYear + 1}.${date.year}"
        else if (date.dayOfMonth > 10 && date.monthOfYear < 9)
          "${date.dayOfMonth}.0${date.monthOfYear + 1}.${date.year}"
        else "${date.dayOfMonth}.${date.monthOfYear + 1}.${date.year}"



  }
}