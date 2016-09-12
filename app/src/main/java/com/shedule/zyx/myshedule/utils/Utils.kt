package com.shedule.zyx.myshedule.utils

import android.content.Context
import com.google.gson.Gson
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.models.Category
import com.shedule.zyx.myshedule.models.Schedule
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
  }
}