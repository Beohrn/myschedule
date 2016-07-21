package com.shedule.zyx.myshedule

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.shedule.zyx.myshedule.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    
    setSupportActionBar(main_toolbar)
    setupDataForViewPager(main_viewpager)
    main_tabs.setupWithViewPager(main_viewpager)
  }

  private fun setupDataForViewPager(viewPager: ViewPager) {
    val adapter = ViewPagerAdapter(supportFragmentManager)
    adapter.addFragment(ScheduleFragment(), "Monday")
    adapter.addFragment(ScheduleFragment(), "Monday")
    adapter.addFragment(ScheduleFragment(), "Monday")
    viewPager.adapter = adapter

    ///svwdskvbs

    //
  }
}
