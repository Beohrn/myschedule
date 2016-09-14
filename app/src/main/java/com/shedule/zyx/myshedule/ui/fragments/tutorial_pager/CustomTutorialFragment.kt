package com.shedule.zyx.myshedule.ui.fragments.tutorial_pager

import android.app.Fragment
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.cleveroad.slidingtutorial.TutorialFragment
import com.cleveroad.slidingtutorial.TutorialPageProvider
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.custom_tutorial_layout.*

/**
 * Created by alexkowlew on 14.09.2016.
 */
class CustomTutorialFragment : TutorialFragment() {

  val TOTAL_PAGES = 3

  override fun getLayoutResId() = R.layout.custom_tutorial_layout

  override fun getIndicatorResId() = R.id.indicatorCustom

  override fun getSeparatorResId() = R.id.separatorCustom

  override fun getButtonSkipResId() = R.id.tvSkipCustom

  override fun getViewPagerResId() = R.id.viewPagerCustom


  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewPagerCustom.offscreenPageLimit = 2

  }

  val tutorialPageProvider = TutorialPageProvider<Fragment> { position ->
    when (position) {
      0 -> FirstPagerFragment()
      1 -> SecondPagerFragment()
      2 -> ThirdPagerFragment()
      else -> throw IllegalArgumentException("Unknown position: " + position)
    }
  }


  override fun provideTutorialOptions() = newTutorialOptionsBuilder(activity)
      .setPagesCount(TOTAL_PAGES)
      .setPagesColors(intArrayOf(ContextCompat.getColor(activity, android.R.color.holo_orange_dark),
          ContextCompat.getColor(activity, android.R.color.holo_green_dark),
          ContextCompat.getColor(activity, android.R.color.holo_blue_dark)))
      .setTutorialPageProvider(tutorialPageProvider)
      .onSkipClickListener {
        activity.finish()
      }
      .build()
}