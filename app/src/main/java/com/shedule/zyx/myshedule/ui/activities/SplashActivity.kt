package com.shedule.zyx.myshedule.ui.activities

import android.os.Bundle
import com.google.firebase.crash.FirebaseCrash.report
import com.shedule.zyx.myshedule.BuildConfig.DEBOUG_ENABLED
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.tutorial.TutorialActivity
import com.shedule.zyx.myshedule.utils.Utils.Companion.isOnline
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * Created by bogdan on 26.09.16.
 */
class SplashActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (auth.currentUser == null) {
      startActivity<TutorialActivity>()
    } else {
      if (prefs.isLogin()) {
        startActivity<MainActivity>()
      } else {
        if (isOnline(applicationContext)) {
          firebaseWrapper.logOut().subscribe({
            prefs.saveChangesCount(0)
            prefs.saveUniverName("")
            prefs.saveFacultyName("")
            startActivity<TutorialActivity>()
          }, { if (DEBOUG_ENABLED) report(it) })
        } else toast(getString(R.string.connection_is_failed))

      }
    }
  }
}