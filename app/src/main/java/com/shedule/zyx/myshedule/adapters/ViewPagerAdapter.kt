package com.shedule.zyx.myshedule.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.shedule.zyx.myshedule.ui.fragments.ScheduleFragment

class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

  //todo use resource, not string variable
  val listTitles = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница")

  override fun getItem(position: Int): Fragment {
    return ScheduleFragment().newInstance(position)
  }

  override fun getCount(): Int {
    return listTitles.size
  }

  override fun getPageTitle(position: Int): CharSequence {
    return listTitles[position]
  }
}