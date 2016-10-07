package com.shedule.zyx.myshedule

import app.voter.xyz.RxFirebase
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.models.Teacher
import com.shedule.zyx.myshedule.utils.Constants.Companion.RATINGS
import com.shedule.zyx.myshedule.utils.Utils.Companion.getKeyByName
import rx.Observable
import java.util.*

/**
 * Created by bogdan on 16.09.16.
 */
class FirebaseWrapper(val ref: DatabaseReference, val prefs: AppPreference, val auth: FirebaseAuth) {

  fun createTeacherRef() = facultyRef().child("teachers")

  private fun facultyRef(): DatabaseReference {
    return ref.child(getKeyByName(prefs.getUniverName() ?: ""))
        .child(getKeyByName(prefs.getFacultyName() ?: ""))
  }

  fun createAccount(): Observable<Void> {
    return Observable.create {
      auth.signInAnonymously().addOnCompleteListener { task ->
        if (task.isSuccessful) {
          it.onNext(null)
          it.onCompleted()
        } else it.onError(task.exception)
      }
    }
  }

  fun getTeachers(): Observable<List<Teacher>?> {
    return Observable.create { sub ->

      RxFirebase.observe(facultyRef())
          .subscribe({
            if (it.value != null) {
              RxFirebase.observeChildAdded(facultyRef())
                  .subscribe({
                    sub.onNext(it.snapshot?.children?.map { it.getValue(Teacher::class.java) })
                    sub.onCompleted()
                  }, {
                    sub.onError(it)
                    sub.onCompleted()
                  })
            } else {
              sub.onNext(null)
              sub.onCompleted()
            }
          }, {
            sub.onError(it)
            sub.onCompleted()
          })
    }
  }

  fun pushTeacher(teachers: List<Teacher>): Observable<Boolean> {
    var loaded = false

    return Observable.create { subscriber ->
      teachers.map { teacher ->
        createTeacherRef().child(getKeyByName(teacher.teacherName)).addListenerForSingleValueEvent(object : ValueEventListener {
          override fun onCancelled(p0: DatabaseError?) {
          }

          override fun onDataChange(data: DataSnapshot?) {
            loaded = true
            if (data?.value == null) {
              createTeacherRef().child(getKeyByName(teacher.teacherName)).setValue(teacher)
              teacherCallback()
            } else {
              (data?.value as HashMap<String, Any>).keys.toList()
                  .filter { it.equals(getKeyByName(teacher.teacherName)) }
                  .firstOrNull()?.let {
                createTeacherRef().child(it)
                    .updateChildren(mapOf(Pair("", ObjectMapper().convertValue(teacher, Map::class.java))))
              }

              teacherCallback()
            }
          }

          private fun teacherCallback() {
            getTeachers().subscribe({
              if (it == null) {
                subscriber.onNext(false)
                subscriber.onCompleted()
              } else {
                RxFirebase.observeChildAdded(facultyRef())
                    .subscribe({
                      subscriber.onNext(true)
                      subscriber.onCompleted()
                    }, {
                      7
                      subscriber.onError(it)
                    })
              }
            }, {
              7
              subscriber.onError(it)
            })
          }
        })
      }

      getTeachers().subscribe({
        if (it == null) {
          if (!loaded) {
            subscriber.onNext(false)
            subscriber.onCompleted()
          }
        } else {
          RxFirebase.observeChildAdded(facultyRef())
              .subscribe({
                if (!loaded) {
                  subscriber.onNext(true)
                  subscriber.onCompleted()
                }
              }, {
                if (BuildConfig.DEBOUG_ENABLED)
                  FirebaseCrash.report(it)
                subscriber.onError(it)
              })
        }
      }, {
        if (BuildConfig.DEBOUG_ENABLED)
          FirebaseCrash.report(it)
        subscriber.onError(it)
      })
    }
  }

  fun pushRating(rating: Double, teacherName: String) {
    createTeacherRef().child(getKeyByName(teacherName)).child(RATINGS).child(auth.currentUser?.uid).setValue(rating)
  }
}