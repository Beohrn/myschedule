package com.shedule.zyx.myshedule.ui.activities

import android.graphics.Color.WHITE
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.crash.FirebaseCrash.report
import com.shedule.zyx.myshedule.BuildConfig.DEBOUG_ENABLED
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R.layout.settings_activity
import com.shedule.zyx.myshedule.R.string.*
import com.shedule.zyx.myshedule.ScheduleApplication.getComponent
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.utils.Constants.Companion.EMPTY_DATA
import com.shedule.zyx.myshedule.utils.Utils.Companion.isOnline
import kotlinx.android.synthetic.main.settings_activity.*
import kotlinx.android.synthetic.main.settings_screen.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers.mainThread
import rx.schedulers.Schedulers.io
import javax.inject.Inject

/**
 * Created by alexkowlew on 08.10.2016.
 */
class SettingsActivity : AppCompatActivity() {

  @Inject
  lateinit var firebaseWrapper: FirebaseWrapper

  @Inject
  lateinit var appPreferences: AppPreference

  var subscription: Subscription? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getComponent().inject(this)
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

    if (appPreferences.getAdminRight()) youAreAdminSettings()
    else becomeAdminSettings()

    admin_button.onClick {
      if (admin_button.text == getString(become_administrator)) {
        if (isOnline(applicationContext)) checkAdmins()
        else toast(getString(connection_is_failed))
      } else if (admin_button.text == getString(not_to_be_an_admin)) {
        if (isOnline(applicationContext)) noToBeAnAdmin()
        else toast(getString(connection_is_failed))
      }
    }
  }

  private fun checkAdmins() {
    val dialog = indeterminateProgressDialog(getString(load))
    subscription = firebaseWrapper.getAdmins(appPreferences.getUniverName(),
        appPreferences.getFacultyName(),
        appPreferences.getGroupName())
        .subscribeOn(io())
        .observeOn(mainThread())
        .subscribe({ admins ->
          dialog.dismiss()
          admins?.let { becomeAdmin(it) }
        }, {
          if (DEBOUG_ENABLED) report(it)
          if (it.message == EMPTY_DATA) {
            dialog.dismiss()
            becomeAdmin(listOf<String>())
          }
        })
  }

  private fun becomeAdmin(admins: List<String>) {
    subscription?.unsubscribe()
    if (admins.size < 2)
      subscription = firebaseWrapper.pushAdmin(appPreferences.getUniverName(),
          appPreferences.getFacultyName(), appPreferences.getGroupName())
          .subscribeOn(io())
          .observeOn(mainThread())
          .subscribe({ key ->
            key?.let {
              appPreferences.saveAdminKey(it)
              appPreferences.saveAdminRights(true)
              youAreAdminSettings()
              toast(getString(you_have_become_an_admin))
            }
          }, { if (DEBOUG_ENABLED) report(it) })
    else toast(getString(you_not_become_admin))
  }

  private fun noToBeAnAdmin() {
    subscription?.unsubscribe()
    val dialog = indeterminateProgressDialog(getString(load))
    subscription = firebaseWrapper.removeAdmin()
        .subscribeOn(io())
        .observeOn(mainThread())
        .subscribe({
          if (it) {
            dialog.dismiss()
            noAdmin()
            becomeAdminSettings()
          }
        }, {
          if (DEBOUG_ENABLED) report(it)
          if (it.message == EMPTY_DATA) {
            dialog.dismiss()
            noAdmin()
            becomeAdminSettings()
          }
        })
  }

  private fun noAdmin() {
    appPreferences.saveAdminKey("")
    appPreferences.saveAdminRights(false)
    admins_count.text = getString(one_admin_is_missing)
    toast(getString(u_no_longer_admin))
  }

  private fun becomeAdminSettings() {
    subscription?.unsubscribe()
    admins_count.text = getString(you_are_not_admin)
    admin_button.text = getString(become_administrator)
  }

  private fun youAreAdminSettings() {
    admins_count.text = getString(you_are_admin)
    admin_button.text = getString(not_to_be_an_admin)
  }

  override fun onDestroy() {
    subscription?.unsubscribe()
    super.onDestroy()
  }
}