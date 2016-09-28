package com.shedule.zyx.myshedule.tutorial

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import app.voter.xyz.auth.fragments.CreateAccountFragment
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.tutorial.tutorial_pager.FirstPagerFragment
import com.shedule.zyx.myshedule.tutorial.tutorial_pager.SecondPagerFragment
import com.shedule.zyx.myshedule.tutorial.tutorial_pager.ThirdPagerFragment
import kotlinx.android.synthetic.main.fitrst_start_activity.*
import org.jetbrains.anko.dip

/**
 * Created by alexkowlew on 14.09.2016.
 */
class TutorialActivity : AppCompatActivity() {

  companion object {
    val DOT_COLOR = "#eceeef"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.fitrst_start_activity)

    tutorial_view_pager.adapter = TutorialPagerAdapter(supportFragmentManager)

    dots.setViewPager(tutorial_view_pager)
    dots.radius = dip(5).toFloat()
    dots.fillColor = Color.parseColor(DOT_COLOR)
    dots.strokeColor = Color.parseColor(DOT_COLOR)
  }

  private class TutorialPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
      return when (position) {
        0 -> FirstPagerFragment()
        1 -> SecondPagerFragment()
        2 -> ThirdPagerFragment()
        else -> CreateAccountFragment()
      }
    }

    override fun getCount() = 4
  }

  override fun onBackPressed() {
    finishAffinity()
  }
}