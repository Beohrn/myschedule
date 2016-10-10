package com.shedule.zyx.myshedule.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.google.gson.Gson
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.models.Category
import com.shedule.zyx.myshedule.models.Category.*
import com.shedule.zyx.myshedule.models.Date
import com.shedule.zyx.myshedule.models.Schedule
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by alexkowlew on 31.08.2016.
 */
class Utils {

  companion object {
    fun getColorByCategory(context: Context, category: Category) = when (category) {
      EXAM -> context.resources.getColor(R.color.mark_red)
      STANDINGS -> context.resources.getColor(R.color.mark_yellow)
      COURSE_WORK -> context.resources.getColor(R.color.mark_orange)
      HOME_EXAM -> context.resources.getColor(R.color.dark_cyan)
      else -> context.resources.getColor(R.color.silver)
    }

    fun toJson(list: ArrayList<Schedule>) = Gson().toJson(list)

    fun getColorByRating(context: Context, assessment: String) = when (assessment) {
      "E" -> context.resources.getColor(R.color.assess_E)
      "D" -> context.resources.getColor(R.color.assess_D)
      "C" -> context.resources.getColor(R.color.assess_C)
      "B" -> context.resources.getColor(R.color.assess_B)
      "A" -> context.resources.getColor(R.color.assess_A)
      else -> 0
    }

    fun getKeyByName(name: String) = name.replace(".", " ")
        .replace("[", "")
        .replace("]", "")
        .replace("$", "")
        .replace("#", "")

    fun getRatingByData(assessments: HashMap<String, Int>): Double {
      var result = 0.0
      assessments.map { result += it.value }
      return result / assessments.size
    }

    fun getLetterByRating(assessment: Double): String {
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

    fun getNormalizedDate(date: Date) =
        if (date.dayOfMonth < 10 && date.monthOfYear < 9)
          "0${date.dayOfMonth}.0${date.monthOfYear + 1}.${date.year}"
        else if (date.dayOfMonth > 10 && date.monthOfYear < 9)
          "${date.dayOfMonth}.0${date.monthOfYear + 1}.${date.year}"
        else "${date.dayOfMonth}.${date.monthOfYear + 1}.${date.year}"

    fun saveAccountImage(context: Context, bitmap: Bitmap) {
      val mediaFile = getMediaFile(context, "", false)
      mediaFile?.let {
        savePhoto(bitmap, it)
      } ?: return
    }

    fun getMediaFile(context: Context, nameDir: String, isDifferent: Boolean): File? {
      val storageDir: File
      if (nameDir.isNullOrBlank()) storageDir = File(defaultDirectory(context))
      else storageDir = File("${defaultDirectory(context)}/HomeWork/$nameDir")

      if (!storageDir.exists())
        storageDir.mkdirs()

      val timeStamp = SimpleDateFormat("ddMMyyyy_HHmmss").format(Date())
      val image: String

      if (isDifferent) image = "HomeWork_${timeStamp}_photo.jpg"
      else image = "Sch_account_photo.jpg"

      return File("${storageDir.path}${File.separator}$image")
    }

    fun getAccountPhoto(context: Context): Bitmap? {
      val image = File("${defaultDirectory(context)}/Sch_account_photo.jpg")
      return BitmapFactory.decodeFile(image.toString())
    }

    fun getHomeWorkImagePath(context: Context, homeWork: String) = File("${defaultDirectory(context)}/HomeWork/$homeWork").listFiles()

    fun saveHomeWorkImage(context: Context, bitmap: Bitmap, homeworkName: String) {
      val photo = getMediaFile(context, homeworkName, true)
      photo?.let {
        savePhoto(bitmap, it)
      } ?: return
    }

    fun deleteHomeWorkPhoto(path: String) = File(path).delete()

    fun deleteHomeWorkDirectory(context: Context, path: String) {
      val dir = File("${Environment.getExternalStorageDirectory()}/Android/data/ ${context.packageName}/Files/HomeWork/$path")
      if (dir.isDirectory) {
        dir.list().map {
          File(dir, it).delete()
        }
      }
      dir.delete()
    }

    fun convertDateString(dateString: String): String {
      val day = dateString.split("-")[0]
      val month = DateFormatSymbols().months[dateString.split("-")[1].toInt()]
      return "$day $month"
    }

    fun defaultDirectory(context: Context) =
        "${Environment.getExternalStorageDirectory()}/Android/data/ ${context.packageName}/Files"

    fun savePhoto(bitmap: Bitmap, file: File) {
      val fos = FileOutputStream(file)
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
      fos.close()
    }
  }
}