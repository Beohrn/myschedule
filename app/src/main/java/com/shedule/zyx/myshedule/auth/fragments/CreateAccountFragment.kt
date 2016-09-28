package app.voter.xyz.auth.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import app.voter.xyz.auth.ReplaceFragmentListener
import com.google.firebase.auth.FirebaseAuth
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.config.AppPreference
import kotlinx.android.synthetic.main.create_account_layout.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onItemClick
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

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    ScheduleApplication.getComponent().inject(this)
    return inflater!!.inflate(R.layout.create_account_layout, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val adapter = ArrayAdapter(context, R.layout.single_text_view, R.id.single_text,
        getString(R.string.universities).split(";").map { it.trim() })

    univer_ET.setAdapter(adapter)
    univer_ET.threshold = 1

    univer_ET.onItemClick { adapterView, view, i, l ->
      univer_ET.setText(adapter.getItem(i))
    }

    faculty_ET.filters = arrayOf<InputFilter>(InputFilter.AllCaps())

    create_account_btn.onClick {
      if (!checkEdiTextIsEmpty(univer_ET) && !checkEdiTextIsEmpty(faculty_ET)) {
        auth.signInAnonymously().addOnCompleteListener {
          if (it.isSuccessful) {
            Log.d("", "${it.result.user.uid}")
            prefs.saveUniverName(univer_ET.text.toString())
            prefs.saveFacultyName(faculty_ET.text.toString())
            firebaseWrapper.updateTeacherRef()
            (activity as ReplaceFragmentListener).changeVisibleFragment(getString(R.string.teachers), TeachersRatingFragment())
          } else Log.d("", "error")
        }
      } else if (checkEdiTextIsEmpty(univer_ET)) {
        univer_ET.error = getString(R.string.input_data)
      } else if (checkEdiTextIsEmpty(faculty_ET)) {
        faculty_ET.error = getString(R.string.input_data)
      }
    }
  }

  fun checkEdiTextIsEmpty(view: EditText) = view.text?.toString().isNullOrEmpty()
}