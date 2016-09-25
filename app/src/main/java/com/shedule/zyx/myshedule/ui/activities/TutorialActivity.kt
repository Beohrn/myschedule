package com.shedule.zyx.myshedule.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.ui.fragments.tutorial_pager.CustomTutorialFragment
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * Created by alexkowlew on 14.09.2016.
 */
class TutorialActivity : AppCompatActivity() {

  @Inject
  lateinit var appPreference: AppPreference

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.fitrst_start_activity)
    ScheduleApplication.getComponent().inject(this)

    if (!appPreference.isFirstTimeLaunch()) {
      startActivity<MainActivity>()
      finish()
    }

    tutorialFragment()
  }

  fun tutorialFragment() {
    fragmentManager
        .beginTransaction()
        .replace(R.id.pager_container, CustomTutorialFragment()).commit()
  }
}