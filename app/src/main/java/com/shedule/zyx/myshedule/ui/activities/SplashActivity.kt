package com.shedule.zyx.myshedule.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crash.FirebaseCrash.report
import com.shedule.zyx.myshedule.BuildConfig.DEBOUG_ENABLED
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.tutorial.TutorialActivity
import com.shedule.zyx.myshedule.utils.Utils.Companion.isOnline
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

/**
 * Created by bogdan on 26.09.16.
 */
class SplashActivity : AppCompatActivity() {

  @Inject
  lateinit var auth: FirebaseAuth

  @Inject
  lateinit var firebaseWraper: FirebaseWrapper

  @Inject
  lateinit var appPreference: AppPreference

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ScheduleApplication.getComponent().inject(this)

    if (auth.currentUser == null) {
      startActivity<TutorialActivity>()
    } else {
      if (appPreference.isLogin()) {
        startActivity<MainActivity>()
      } else {
        if (isOnline(applicationContext)) {
          firebaseWraper.logOut().subscribe({
            appPreference.saveChangesCount(0)
            appPreference.saveUniverName("")
            appPreference.saveFacultyName("")
            startActivity<TutorialActivity>()
          }, { if (DEBOUG_ENABLED) report(it) })
        } else toast(getString(R.string.connection_is_failed))

      }
    }
  }
}