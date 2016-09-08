package com.shedule.zyx.myshedule.adapters

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.shedule.zyx.myshedule.ui.fragments.PeriodicScheduleFragment
import com.shedule.zyx.myshedule.ui.fragments.RandomScheduleFragment

/**
 * Created by alexkowlew on 08.09.2016.
 */
class RepeatDialogPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {

  val listOfTitles = listOf("Периодически", "Произвольные даты")

  override fun getItem(position: Int) = when (position) {
    0 -> PeriodicScheduleFragment()
    else -> RandomScheduleFragment()
  }

  override fun getCount() = listOfTitles.size

  override fun getPageTitle(position: Int) = listOfTitles[position]
}