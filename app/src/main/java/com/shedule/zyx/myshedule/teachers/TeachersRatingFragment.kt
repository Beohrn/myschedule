package app.voter.xyz.auth.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.voter.xyz.RxFirebase
import com.google.firebase.auth.FirebaseAuth
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.managers.ScheduleManager
import com.shedule.zyx.myshedule.teachers.TeachersAdapter
import com.shedule.zyx.myshedule.teachers.TeachersAdapter.OnTeacherClickListener
import com.shedule.zyx.myshedule.widget.TeacherView.OnRatingClickListener
import kotlinx.android.synthetic.main.teachers_rating_layout.*
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.toast
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by bogdan on 13.09.16.
 */
class TeachersRatingFragment : Fragment() {

  @Inject
  lateinit var auth: FirebaseAuth

  @Inject
  lateinit var scheduleManager: ScheduleManager

  @Inject
  lateinit var firebase: FirebaseWrapper

  lateinit var teachersAdapter: TeachersAdapter

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    ScheduleApplication.getComponent().inject(this)
    return inflater!!.inflate(R.layout.teachers_rating_layout, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

      val dialog = indeterminateProgressDialog(getString(R.string.load))
      dialog.setCanceledOnTouchOutside(false)
      dialog.show()
      firebase.pushTeacher(scheduleManager.getTeachers().filter { it.teacherName.isNotEmpty() })
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            if (it) toast("true") else toast("false")
            dialog.dismiss()
          }, {
            dialog.dismiss()
            toast(getString(R.string.download_error))
          })

    RxFirebase.observeChildAdded(firebase.createTeacherRef()).subscribe({
      teachers_recycle_view.smoothScrollToPosition(teachersAdapter.itemCount)
    }, {})

    teachersAdapter = TeachersAdapter(context, firebase.createTeacherRef(),
        activity as OnTeacherClickListener, activity as OnRatingClickListener)
    teachers_recycle_view.layoutManager = LinearLayoutManager(context)
    teachers_recycle_view.adapter = teachersAdapter
  }
}