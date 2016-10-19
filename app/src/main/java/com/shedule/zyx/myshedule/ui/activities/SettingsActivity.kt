package com.shedule.zyx.myshedule.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.graphics.Color.WHITE
import android.net.Uri.parse
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
import de.cketti.mailto.EmailIntentBuilder
import kotlinx.android.synthetic.main.settings_activity.*
import kotlinx.android.synthetic.main.settings_screen.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

/**
 * Created by alexkowlew on 08.10.2016.
 */
class SettingsActivity: BaseActivity() {

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
      if (prefs.getAdminRight()) youAreAdminSettings()
      else becomeAdminSettings()
    }

    manager_button.onClick {
      if (manager_button.text == getString(R.string.become_manager)) {
        if (isOnline(this)) setEnableAdmin()
        else toast(getString(connection_is_failed))
      } else if (manager_button.text == getString(R.string.not_to_be_an_manager)) {
        if (isOnline(applicationContext)) setDisableAdmin()
        else toast(getString(connection_is_failed))
      }
    }

    open_our_group.onClick { startActivity(Intent(ACTION_VIEW, parse("https://vk.com/club129716882"))) }

    write_to_us.onClick { sendEmail() }
  }

  fun sendEmail() =
      startActivity(EmailIntentBuilder.from(this)
          .to(getString(R.string.email))
          .subject(getString(feedback))
          .build())

  private fun setEnableAdmin() {
    showProgressDialog(getString(load))
    subscription = firebaseWrapper.pushAdmin()
        .toMainThread()
        .subscribe({ key ->
          key?.let {
            hideProgressDialog()
            if (it != ADMIN_IS_EXISTS) {
              prefs.saveAdminKey(it)
              prefs.saveAdminRights(true)
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

  private fun setDisableAdmin() {
    subscription?.unsubscribe()
    showProgressDialog(getString(load))
    subscription = firebaseWrapper.removeAdmin()
        .toMainThread()
        .subscribe({
          if (it) {
            hideProgressDialog()
            clearAdmin()
            becomeAdminSettings()
          }
        }, {
          if (DEBOUG_ENABLED) report(it)
          if (it.message == EMPTY_DATA) {
            hideProgressDialog()
            clearAdmin()
            becomeAdminSettings()
          }
        })
  }

  private fun clearAdmin() {
    prefs.saveAdminKey("")
    prefs.saveAdminRights(false)
    toast(getString(R.string.u_no_longer_manager))
  }

  private fun becomeAdminSettings() {
    manager_info.text = getString(R.string.before_do_action_become_manager)
    manager_button.text = getString(R.string.become_manager)
  }

  private fun youAreAdminSettings() {
    manager_info.text = getString(R.string.before_do_action_become_manager)
    manager_button.text = getString(R.string.not_to_be_an_manager)
  }
}