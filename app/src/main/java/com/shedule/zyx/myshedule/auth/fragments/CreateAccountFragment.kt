package com.shedule.zyx.myshedule.auth.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import com.google.firebase.crash.FirebaseCrash.report
import com.shedule.zyx.myshedule.BuildConfig.DEBOUG_ENABLED
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.string.*
import com.shedule.zyx.myshedule.ui.activities.MainActivity
import com.shedule.zyx.myshedule.ui.fragments.BaseFragment
import com.shedule.zyx.myshedule.utils.Constants.Companion.EMPTY_DATA
import com.shedule.zyx.myshedule.utils.Constants.Companion.TEMP
import com.shedule.zyx.myshedule.utils.Utils
import com.shedule.zyx.myshedule.utils.Utils.Companion.isOnline
import com.shedule.zyx.myshedule.utils.toMainThread
import kotlinx.android.synthetic.main.create_account_layout.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onItemClick
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import rx.Subscription

/**
 * Created by bogdan on 13.09.16.
 */
class CreateAccountFragment : BaseFragment() {
  override var contentView = R.layout.create_account_layout

  lateinit var facultyWatcher: TextWatcher
  lateinit var groupWatcher: TextWatcher

  var subscription: Subscription? = null

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val adapter = ArrayAdapter(context, R.layout.single_text_view, R.id.single_text,
        Utils.getUniversities(context))

    univer_ET.setAdapter(adapter)
    univer_ET.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

    univer_ET.onItemClick { adapterView, view, i, l ->
      univer_ET.setText(adapter.getItem(i))
    }

    universities_button.onClick {
      hideKyboard()
      if (isOnline(context)) {
        subscription?.unsubscribe()
        showDialog(getString(R.string.load))
        subscription = firebaseWrapper.getUniversity()
            .toMainThread()
            .doOnTerminate { hideDialog() }
            .subscribe({ universities ->
              hideDialog()
              if (universities != null) {
                selector(null, universities) {
                  univer_ET.setText(universities[it])
                  univer_ET.error = null
                  faculty_ET.setText("")
                  subscription?.unsubscribe()
                }
              } else toast(getString(no_universities))
            }, {
              hideDialog()
              if (it.message == EMPTY_DATA) toast(getString(R.string.no_universities))
              if (DEBOUG_ENABLED) report(it)
            })
      } else toast(getString(connection_is_failed))
    }

    faculty_button.onClick {
      hideKyboard()
      if (isOnline(context)) {
        if (!checkEdiTextIsEmpty(univer_ET)) {
          subscription?.unsubscribe()
          showDialog(getString(R.string.load))
          subscription = firebaseWrapper.getFaculty(univer_ET.text.toString())
              .toMainThread()
              .doOnTerminate { hideDialog() }
              .subscribe({ faculty ->
                hideDialog()
                if (faculty != null) {
                  selector(null, faculty) {
                    faculty_ET.setText(faculty[it])
                    faculty_ET.error = null
                    subscription?.unsubscribe()
                  }
                } else toast(getString(no_faculties))
              }, {
                hideDialog()
                if (it.message == EMPTY_DATA) toast(getString(R.string.no_faculties))
                if (DEBOUG_ENABLED) report(it)
              })
        } else univer_ET.error = getString(type_the_university_name)
      } else toast(getString(connection_is_failed))
    }

    group_button.onClick {
      hideKyboard()
      if (isOnline(context)) {
        if (!checkEdiTextIsEmpty(faculty_ET) && !checkEdiTextIsEmpty(univer_ET)) {
          showDialog(getString(R.string.load))
          subscription?.unsubscribe()
          subscription = firebaseWrapper.getGroups(faculty_ET.text.toString(),
              univer_ET.text.toString())
              .toMainThread()
              .doOnTerminate { hideDialog() }
              .subscribe({
                it?.let { groups ->
                  hideDialog()
                  if (groups.size != 0) {
                    selector(null, groups) { index ->
                      group_ET.setText(groups[index])
                      group_ET.error = null
                      subscription?.unsubscribe()
                    }
                  } else toast(getString(groups_not_found))
                } ?: toast(getString(groups_not_found))
              }, {
                hideDialog()
                if (it.message == EMPTY_DATA)
                  toast(getString(R.string.groups_not_found))
                if (DEBOUG_ENABLED) report(it)
              })
        } else if (checkEdiTextIsEmpty(faculty_ET)) faculty_ET.error = getString(type_the_faculty_name)
        else if (checkEdiTextIsEmpty(univer_ET)) univer_ET.error = getString(type_the_university_name)
      } else toast(getString(connection_is_failed))
    }

    facultyWatcher = object : TextWatcher {
      override fun afterTextChanged(s: Editable?) { }
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        faculty_ET.removeTextChangedListener(facultyWatcher)
        faculty_ET.setText(faculty_ET.text.toString().replace("Ы", "І").replace("Э", "Е"))
        faculty_ET.setSelection(faculty_ET.text.toString().length)
        faculty_ET.addTextChangedListener(facultyWatcher)
      }
    }

    groupWatcher = object : TextWatcher {
      override fun afterTextChanged(s: Editable?) { }
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

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

    create_account_btn.onClick {
      hideKyboard()
      if (isOnline(context)) {

        if (!checkEdiTextIsEmpty(univer_ET) && !checkEdiTextIsEmpty(faculty_ET) && !checkEdiTextIsEmpty(group_ET)) {
          showDialog(getString(R.string.authentication))
          firebaseWrapper.createAccount()
              .toMainThread()
              .doOnTerminate { hideDialog() }
              .subscribe({
                hideDialog()
                prefs.saveUniverName(univer_ET.text.toString().trim())
                prefs.saveFacultyName(faculty_ET.text.toString().trim())
                prefs.saveGroupName(group_ET.text.toString().trim())
                prefs.saveLogin(true)
                firebaseWrapper.groupRef().child(TEMP).setValue(TEMP)
                startActivity<MainActivity>()
              }, {
                hideDialog()
                if (DEBOUG_ENABLED) report(it)
                toast(getString(authentication_error))
              })
        } else if (checkEdiTextIsEmpty(univer_ET)) {
          univer_ET.error = getString(type_the_university_name)
          if (checkEdiTextIsEmpty(faculty_ET))
            faculty_ET.error = getString(type_the_faculty_name)
          if (checkEdiTextIsEmpty(group_ET))
            group_ET.error = getString(type_the_group_name)
        } else if (checkEdiTextIsEmpty(faculty_ET)) {
          faculty_ET.error = getString(type_the_faculty_name)
          if (checkEdiTextIsEmpty(univer_ET))
            univer_ET.error = getString(type_the_university_name)
          if (checkEdiTextIsEmpty(group_ET))
            group_ET.error = getString(type_the_group_name)
        } else if (checkEdiTextIsEmpty(group_ET)) {
          group_ET.error = getString(type_the_group_name)
          if (checkEdiTextIsEmpty(faculty_ET))
            faculty_ET.error = getString(type_the_faculty_name)
          if (checkEdiTextIsEmpty(univer_ET))
            univer_ET.error = getString(type_the_university_name)
        }
      } else toast(getString(connection_is_failed))
    }
  }

  override fun onDestroy() {
    subscription?.unsubscribe()
    super.onDestroy()
  }

  fun checkEdiTextIsEmpty(view: EditText) = view.text?.toString().isNullOrEmpty()
}