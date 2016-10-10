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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crash.FirebaseCrash
import com.shedule.zyx.myshedule.BuildConfig
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.ui.activities.MainActivity
import kotlinx.android.synthetic.main.create_account_layout.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onItemClick
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
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
  lateinit var univerWatcher: TextWatcher

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    ScheduleApplication.getComponent().inject(this)
    return inflater!!.inflate(R.layout.create_account_layout, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val adapter = ArrayAdapter(context, R.layout.single_text_view, R.id.single_text,
        getString(R.string.universities).split(";").map(String::trim))

    univer_ET.setAdapter(adapter)
    univer_ET.threshold = 1

    univer_ET.onItemClick { adapterView, view, i, l ->
      univer_ET.setText(adapter.getItem(i))
    }

    univerWatcher = object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {

      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        univer_ET.removeTextChangedListener(univerWatcher)
        univer_ET.setText(univer_ET.text.toString().replace("Ы", "І").replace("Э", "Е")
            .replace("ы", "і").replace("э", "е"))
        univer_ET.setSelection(univer_ET.text.toString().length)
        univer_ET.addTextChangedListener(univerWatcher)
      }
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
    univer_ET.addTextChangedListener(univerWatcher)

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

  fun checkEdiTextIsEmpty(view: EditText) = view.text?.toString().isNullOrEmpty()
}