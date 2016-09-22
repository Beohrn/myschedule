package com.shedule.zyx.myshedule.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ui.fragments.ScheduleFragment

class ViewPagerAdapter(context: Context, manager: FragmentManager) : FragmentPagerAdapter(manager) {

  val listTitles = listOf(context.getString(R.string.monday),
      context.getString(R.string.tuesday),
      context.getString(R.string.wednesday),
      context.getString(R.string.thursday),
      context.getString(R.string.friday))

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