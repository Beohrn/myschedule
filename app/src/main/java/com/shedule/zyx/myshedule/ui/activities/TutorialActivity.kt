package com.shedule.zyx.myshedule.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ui.fragments.tutorial_pager.CustomTutorialFragment

/**
 * Created by alexkowlew on 14.09.2016.
 */
class TutorialActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.fitrst_start_activity)
    tutorialFragment()
  }

  fun tutorialFragment() {
    fragmentManager
        .beginTransaction()
        .replace(R.id.pager_container, CustomTutorialFragment()).commit()
  }
}