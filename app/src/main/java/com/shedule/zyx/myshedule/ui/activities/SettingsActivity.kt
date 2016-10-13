package com.shedule.zyx.myshedule.ui.activities

import android.app.Activity
import android.graphics.Color.WHITE
import android.os.Bundle
import com.google.firebase.crash.FirebaseCrash.report
import com.shedule.zyx.myshedule.BuildConfig.DEBOUG_ENABLED
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.layout.settings_activity
import com.shedule.zyx.myshedule.R.string.*
import com.shedule.zyx.myshedule.utils.Constants.Companion.ADMIN_IS_EXISTS
import com.shedule.zyx.myshedule.utils.Constants.Companion.EMPTY_DATA
import com.shedule.zyx.myshedule.utils.Utils.Companion.isOnline
import com.shedule.zyx.myshedule.utils.toMainThread
import kotlinx.android.synthetic.main.settings_activity.*
import kotlinx.android.synthetic.main.settings_screen.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

/**
 * Created by alexkowlew on 08.10.2016.
 */
class SettingsActivity : BaseActivity() {

  companion object {
    val MANAGER_REQUEST = 3292
    val BECOME_MANAGER_KEY = "manager_key"
  }

  var isBecomeManager = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(settings_activity)

    subscription?.unsubscribe()
    setSupportActionBar(settings_toolbar)
    supportActionBar?.let {
      with(it) {
        setHomeButtonEnabled(true)
        setDisplayHomeAsUpEnabled(true)
        title = getString(settings)
      }
    }

    with(settings_toolbar) {
      setTitleTextColor(WHITE)
      setNavigationOnClickListener { finish() }
    }

    isBecomeManager = intent.getBooleanExtra(BECOME_MANAGER_KEY, false)

    if (isBecomeManager) {
      manager_info.text = getString(R.string.before_do_action_become_manager)
      manager_button.text = getString(R.string.become_manager)
    } else {
      if (appPreference.getAdminRight()) youAreAdminSettings()
      else becomeAdminSettings()
    }

    manager_button.onClick {
      if (manager_button.text == getString(R.string.become_manager)) {
        if (isOnline(applicationContext)) becomeManager()
        else toast(getString(connection_is_failed))
      } else if (manager_button.text == getString(R.string.not_to_be_an_manager)) {
        if (isOnline(applicationContext)) noToBeManager()
        else toast(getString(connection_is_failed))
      }
    }
  }

  private fun becomeManager() {
    showProgressDialog(getString(load))
    subscription = firebaseWrapper.pushAdmin(appPreference.getUniverName(),
        appPreference.getFacultyName(), appPreference.getGroupName())
        .toMainThread()
        .subscribe({ key ->
          key?.let {
            hideProgressDialog()
            if (it != ADMIN_IS_EXISTS) {
              appPreference.saveAdminKey(it)
              appPreference.saveAdminRights(true)
              youAreAdminSettings()
              toast(getString(R.string.you_have_become_an_manager))
              subscription?.unsubscribe()
              if (isBecomeManager) {
                setResult(Activity.RESULT_OK)
                finish()
              } else {}
            } else {
              toast(getString(R.string.you_not_become_manager))
              subscription?.unsubscribe()
            }
          }
        }, {
          if (DEBOUG_ENABLED) report(it)
          hideProgressDialog()
        })
  }

  private fun noToBeManager() {
    subscription?.unsubscribe()
    showProgressDialog(getString(load))
    subscription = firebaseWrapper.removeAdmin()
        .toMainThread()
        .subscribe({
          if (it) {
            hideProgressDialog()
            noAdmin()
            becomeAdminSettings()
          }
        }, {
          if (DEBOUG_ENABLED) report(it)
          if (it.message == EMPTY_DATA) {
            hideProgressDialog()
            noAdmin()
            becomeAdminSettings()
          }
        })
  }

  private fun noAdmin() {
    appPreference.saveAdminKey("")
    appPreference.saveAdminRights(false)
    toast(getString(R.string.u_no_longer_manager))
  }

  private fun becomeAdminSettings() {
    subscription?.unsubscribe()
    manager_info.text = getString(R.string.you_are_not_manager)
    manager_button.text = getString(R.string.become_manager)
  }

  private fun youAreAdminSettings() {
    manager_info.text = getString(R.string.you_are_manager)
    manager_button.text = getString(R.string.not_to_be_an_manager)
  }
}