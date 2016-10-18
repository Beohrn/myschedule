package com.shedule.zyx.myshedule.ui.activities

import android.os.Bundle
import com.shedule.zyx.myshedule.tutorial.TutorialActivity
import org.jetbrains.anko.startActivity

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
        prefs.saveChangesCount(0)
        prefs.saveUniverName("")
        prefs.saveFacultyName("")
        prefs.saveGroupName("")
        startActivity<TutorialActivity>()
      }
    }
  }
}