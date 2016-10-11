package com.shedule.zyx.myshedule.auth.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crash.FirebaseCrash.report
import com.shedule.zyx.myshedule.BuildConfig.DEBOUG_ENABLED
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.string.*
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.ui.activities.MainActivity
import com.shedule.zyx.myshedule.utils.Constants.Companion.EMPTY_DATA
import com.shedule.zyx.myshedule.utils.Utils
import com.shedule.zyx.myshedule.utils.Utils.Companion.isOnline
import kotlinx.android.synthetic.main.create_account_layout.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onItemClick
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers.mainThread
import rx.schedulers.Schedulers.io
import javax.inject.Inject

/**
 * Created by bogdan on 13.09.16.
 */
class CreateAccountFragment : Fragment() {

  @Inject
  lateinit var auth: FirebaseAuth

  @Inject
  lateinit var prefs: AppPreference

  @Inject
  lateinit var firebaseWrapper: FirebaseWrapper

  lateinit var facultyWatcher: TextWatcher
  lateinit var groupWatcher: TextWatcher

  var subscription: Subscription? = null
  var adminsCount = 0

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    ScheduleApplication.getComponent().inject(this)
    return inflater!!.inflate(R.layout.create_account_layout, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val adapter = ArrayAdapter(context, R.layout.single_text_view, R.id.single_text,
        Utils.getUniversities(context))

    univer_ET.setAdapter(adapter)
    univer_ET.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

    univer_ET.onItemClick { adapterView, view, i, l ->
      univer_ET.setText(adapter.getItem(i))
    }

    remote_list.onClick {
      if (isOnline(context)) {
        subscription?.unsubscribe()
        val dialog = indeterminateProgressDialog(getString(load))
        subscription = firebaseWrapper.getUniversity()
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({ universities ->
              dialog.dismiss()
              if (universities != null) {
                selector(null, universities) {
                  univer_ET.setText(universities[it])
                  univer_ET.error = null
                  faculty_ET.setText("")
                  subscription?.unsubscribe()
                }
              } else toast(getString(no_universities))
            }, {
              if (it.message == EMPTY_DATA)
                toast(getString(R.string.no_data))
              if (DEBOUG_ENABLED) report(it)
              dialog.dismiss()
            })
      } else toast(getString(connection_is_failed))
    }

    remote_faculty_list.onClick {
      if (isOnline(context)) {
        if (!univer_ET.text.isNullOrBlank()) {
          subscription?.unsubscribe()
          val dialog = indeterminateProgressDialog(getString(load))
          subscription = firebaseWrapper.getFaculty(univer_ET.text.toString())
              .subscribeOn(io())
              .observeOn(mainThread())
              .subscribe({ faculty ->
                dialog.dismiss()
                if (faculty != null) {
                  selector(null, faculty) {
                    faculty_ET.setText(faculty[it])
                    faculty_ET.error = null
                    subscription?.unsubscribe()
                  }
                } else toast(getString(no_faculties))
              }, {
                if (it.message == EMPTY_DATA)
                  toast(getString(R.string.no_data))
                if (DEBOUG_ENABLED) report(it)
                dialog.dismiss()
              })
        } else univer_ET.error = getString(type_the_university_name)
      } else toast(getString(connection_is_failed))
    }

    remote_group_list.onClick {
      if (isOnline(context)) {
        if (!faculty_ET.text.isNullOrEmpty() && !univer_ET.text.isNullOrEmpty()) {
          val dialog = indeterminateProgressDialog(getString(load))
          subscription?.unsubscribe()
          subscription = firebaseWrapper.getGroups(faculty_ET.text.toString(),
              univer_ET.text.toString())
              .subscribeOn(io())
              .observeOn(mainThread())
              .subscribe({
                dialog.dismiss()
                it?.let { groups ->
                  if (groups.size != 0) {
                    selector(null, groups) { index ->
                      group_ET.setText(groups[index])
                      group_ET.error = null
                      subscription?.unsubscribe()
                    }
                  } else toast(getString(groups_not_found))
                } ?: toast(getString(groups_not_found))
              }, {
                if (it.message == EMPTY_DATA)
                  toast(getString(R.string.no_data))
                if (DEBOUG_ENABLED) report(it)
                dialog.dismiss()
              })
        } else if (faculty_ET.text.isNullOrBlank()) faculty_ET.error = getString(type_the_faculty_name)
        else if (univer_ET.text.isNullOrBlank()) univer_ET.error = getString(type_the_university_name)
      } else toast(getString(connection_is_failed))
    }

    facultyWatcher = object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {

      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        faculty_ET.removeTextChangedListener(facultyWatcher)
        faculty_ET.setText(faculty_ET.text.toString().replace("Ы", "І").replace("Э", "Е"))
        faculty_ET.setSelection(faculty_ET.text.toString().length)
        faculty_ET.addTextChangedListener(facultyWatcher)
      }
    }

    groupWatcher = object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {

      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        group_ET.removeTextChangedListener(groupWatcher)
        group_ET.setText(group_ET.text.toString().replace("Ы", "І").replace("Э", "Е"))
        group_ET.setSelection(group_ET.text.toString().length)
        group_ET.addTextChangedListener(groupWatcher)
      }
    }

    group_ET.addTextChangedListener(groupWatcher)

    faculty_ET.filters = arrayOf<InputFilter>(android.text.InputFilter.AllCaps())
    faculty_ET.addTextChangedListener(facultyWatcher)
    admin.setOnCheckedChangeListener { compoundButton, b ->
      if (!group_ET.text.isNullOrEmpty()) {
        if (b) checkAdmins(true)
      } else {
        admin.isChecked = false
        group_ET.error = getString(type_the_group_name)
      }
    }

    create_account_btn.onClick {
      if (isOnline(context)) {

        if (!checkEdiTextIsEmpty(univer_ET) && !checkEdiTextIsEmpty(faculty_ET) && !checkEdiTextIsEmpty(group_ET)) {
            val dialog = indeterminateProgressDialog(getString(authentication))
            dialog.show()
            firebaseWrapper.createAccount()
                .doOnTerminate { dialog.dismiss() }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({
                  prefs.saveUniverName(univer_ET.text.toString().trim())
                  prefs.saveFacultyName(faculty_ET.text.toString().trim())
                  prefs.saveGroupName(group_ET.text.toString().trim())
                  prefs.saveLogin(true)
                  if (admin.isChecked) createGroup()
                  startActivity<MainActivity>()
                }, {
                  if (DEBOUG_ENABLED) report(it)
                  toast(getString(authentication_error))
                })
        } else if (checkEdiTextIsEmpty(univer_ET)) {
          univer_ET.error = getString(input_data)
          if (checkEdiTextIsEmpty(faculty_ET))
            faculty_ET.error = getString(input_data)
          if (checkEdiTextIsEmpty(group_ET))
            group_ET.error = getString(input_data)
        } else if (checkEdiTextIsEmpty(faculty_ET)) {
          faculty_ET.error = getString(input_data)
          if (checkEdiTextIsEmpty(univer_ET))
            univer_ET.error = getString(input_data)
          if (checkEdiTextIsEmpty(group_ET))
            group_ET.error = getString(input_data)
        } else if (checkEdiTextIsEmpty(group_ET)) {
          group_ET.error = getString(input_data)
          if (checkEdiTextIsEmpty(faculty_ET))
            faculty_ET.error = getString(input_data)
          if (checkEdiTextIsEmpty(univer_ET))
            univer_ET.error = getString(input_data)
        }
      } else toast(getString(connection_is_failed))
    }
  }

  private fun createGroup() {
    subscription?.unsubscribe()
    if (adminsCount < 2)
      subscription = firebaseWrapper.pushAdmin(univer_ET.text.toString(),
          faculty_ET.text.toString(), group_ET.text.toString())
          .subscribeOn(io())
          .observeOn(mainThread())
          .subscribe({ key ->
            key?.let {
              prefs.saveAdminRights(true)
              prefs.saveChangesCount(0)
              prefs.saveAdminKey(it)
            }
          }, {
            if (it.message == EMPTY_DATA)
              toast(getString(R.string.no_data))
            if (DEBOUG_ENABLED) report(it)
          })
    else toast(getString(you_not_become_admin))
  }

  override fun onStop() {
    subscription?.unsubscribe()
    super.onStop()
  }

  private fun checkAdmins(show: Boolean) {
    subscription?.unsubscribe()
    var dialog: ProgressDialog? = null
    if (show)
      dialog = indeterminateProgressDialog(getString(check))

    subscription = firebaseWrapper.getAdmins(univer_ET.text.toString(),
        faculty_ET.text.toString(), group_ET.text.toString())
        .subscribeOn(io())
        .observeOn(mainThread())
        .subscribe({ admins ->
          dialog?.dismiss()
          admins?.let {
            if (it.size < 2) {
              admin.isChecked = true
              adminsCount = it.size
              toast(getString(you_have_become_an_admin))
            } else {
              admin.isChecked = false
              adminsCount = it.size
              toast(getString(you_not_become_admin))
            }
          }
        }, {
          if (DEBOUG_ENABLED) report(it)
          dialog?.dismiss()
          if (it.message == EMPTY_DATA) {
            admin.isChecked = true
            toast(getString(you_have_become_an_admin))
          }
        })
  }

  fun checkEdiTextIsEmpty(view: EditText) = view.text?.toString().isNullOrEmpty()
}