package com.shedule.zyx.myshedule.auth.fragments

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
import com.google.firebase.crash.FirebaseCrash
import com.shedule.zyx.myshedule.BuildConfig
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.R.string.*
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.ui.activities.MainActivity
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
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
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

  var subscription: Subscription? = null

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
        val dialog = indeterminateProgressDialog(getString(R.string.load))
        subscription = firebaseWrapper.getUniversity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ universities ->
              dialog.dismiss()
              if (universities != null) {
                selector(null, universities) {
                  univer_ET.setText(universities[it])
                  faculty_ET.setText("")
                }
              } else toast(getString(no_universities))
            }, {
              if (BuildConfig.DEBOUG_ENABLED)
                FirebaseCrash.report(it)
              dialog.dismiss()
            })
      } else toast(getString(R.string.connection_is_failed))
    }

    remote_faculty_list.onClick {
      if (isOnline(context)) {
        if (!univer_ET.text.isNullOrBlank()) {
          val dialog = indeterminateProgressDialog(getString(R.string.load))
          subscription = firebaseWrapper.getFaculty(univer_ET.text.toString())
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe({ faculty ->
                dialog.dismiss()
                if (faculty != null) {
                  selector(null, faculty) { faculty_ET.setText(faculty[it]) }
                } else toast(getString(no_faculties))
              }, {
                if (BuildConfig.DEBOUG_ENABLED)
                  FirebaseCrash.report(it)
                dialog.dismiss()
              })
        } else toast(getString(type_the_university_name))
      } else toast(getString(R.string.connection_is_failed))
    }

    remote_group_list.onClick {
      if (isOnline(context)) {
        if (!faculty_ET.text.isNullOrBlank() && !univer_ET.text.isNullOrBlank()) {
          val dialog = indeterminateProgressDialog(getString(R.string.load))
          subscription = firebaseWrapper.getGroups(faculty_ET.text.toString(),
              univer_ET.text.toString())
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe({
                dialog.dismiss()
                it?.let { groups ->
                  if (groups.size != 0) {
                    selector(null, groups) { index ->
                      group_ET.setText(groups[index])
                      group_ET.filters = arrayOf<InputFilter>(android.text.InputFilter.AllCaps())
                      checkAdmins()
                    }
                  } else toast(getString(groups_not_found))
                } ?: toast(getString(groups_not_found))
              }, {
                if (BuildConfig.DEBOUG_ENABLED)
                  FirebaseCrash.report(it)
                dialog.dismiss()
              })
        } else if (faculty_ET.text.isNullOrBlank()) toast(getString(R.string.type_the_faculty_name))
        else if (univer_ET.text.isNullOrBlank()) toast(getString(type_the_university_name))
      } else toast(getString(R.string.connection_is_failed))
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

    faculty_ET.filters = arrayOf<InputFilter>(android.text.InputFilter.AllCaps())
    faculty_ET.addTextChangedListener(facultyWatcher)

    create_account_btn.onClick {
      if (!checkEdiTextIsEmpty(univer_ET) && !checkEdiTextIsEmpty(faculty_ET)) {
        val dialog = indeterminateProgressDialog(getString(R.string.authentication))
        dialog.show()
        firebaseWrapper.createAccount()
            .doOnTerminate { dialog.dismiss() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              prefs.saveUniverName(univer_ET.text.toString().trim())
              prefs.saveFacultyName(faculty_ET.text.toString().trim())
              prefs.saveGroupName(group_ET.text.toString().trim())
              prefs.saveAdminRights(admin.isChecked)

              if (admin.isChecked)
                createGroup()

              startActivity<MainActivity>()
            }, {
              if (BuildConfig.DEBOUG_ENABLED)
                FirebaseCrash.report(it)
              toast(getString(R.string.authentication_error))
            })
      } else if (checkEdiTextIsEmpty(univer_ET)) {
        univer_ET.error = getString(R.string.input_data)
      } else if (checkEdiTextIsEmpty(faculty_ET)) {
        faculty_ET.error = getString(R.string.input_data)
      }
    }
  }

  private fun createGroup() {
    firebaseWrapper.pushGroup()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({}, {
          if (BuildConfig.DEBOUG_ENABLED)
            FirebaseCrash.report(it)
        })
  }

  override fun onStop() {
    super.onStop()
    subscription?.unsubscribe()
  }

  private fun checkAdmins() {
    firebaseWrapper.getAdmins()
      .subscribe({ admins ->
        admins?.let {
          if (it.size < 2) admin.visibility = View.VISIBLE
          else admin.visibility = View.GONE
        }
      }, {})
  }

  fun checkEdiTextIsEmpty(view: EditText) = view.text?.toString().isNullOrEmpty()
}