package com.shedule.zyx.myshedule.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.tutorial.TutorialActivity
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * Created by bogdan on 26.09.16.
 */
class SplashActivity : AppCompatActivity() {

  @Inject
  lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ScheduleApplication.getComponent().inject(this)

    if (auth.currentUser == null) {
      startActivity<TutorialActivity>()
    } else { startActivity<MainActivity>() }
  }
}