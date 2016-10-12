package com.shedule.zyx.myshedule.ui.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.config.AppPreference
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import javax.inject.Inject

/**
 * Created by alexkowlew on 12.10.2016.
 */
abstract class BaseFragment: Fragment() {

  @Inject
  lateinit var auth: FirebaseAuth

  @Inject
  lateinit var prefs: AppPreference

  @Inject
  lateinit var firebaseWrapper: FirebaseWrapper

  lateinit var dialog: ProgressDialog

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

  }

  protected abstract var contentView: Int

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    ScheduleApplication.getComponent().inject(this)
    if (contentView == 0) {
      throw IllegalStateException("Layout can't be 0")
    } else {
      return inflater!!.inflate(contentView, container, false)
    }
  }

  fun hideKyboard() {
    val view = activity.currentFocus
    if (view != null) {
      val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
  }

  fun showDialog() {
    dialog = indeterminateProgressDialog(getString(R.string.authentication))
    if (!dialog.isShowing) {
      dialog.show()
    } else dialog.dismiss()
  }

  fun hideDialog() {
    dialog.hide()
  }

}