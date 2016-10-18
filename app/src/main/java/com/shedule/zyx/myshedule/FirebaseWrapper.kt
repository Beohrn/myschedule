package com.shedule.zyx.myshedule

import app.voter.xyz.RxFirebase
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crash.FirebaseCrash.report
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.shedule.zyx.myshedule.BuildConfig.DEBOUG_ENABLED
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.events.Event
import com.shedule.zyx.myshedule.models.Schedule
import com.shedule.zyx.myshedule.models.Teacher
import com.shedule.zyx.myshedule.utils.Constants.Companion.ADMIN
import com.shedule.zyx.myshedule.utils.Constants.Companion.ADMIN_IS_EXISTS
import com.shedule.zyx.myshedule.utils.Constants.Companion.CHANGES_COUNT
import com.shedule.zyx.myshedule.utils.Constants.Companion.EMPTY_DATA
import com.shedule.zyx.myshedule.utils.Constants.Companion.GROUP_CREATED
import com.shedule.zyx.myshedule.utils.Constants.Companion.RATINGS
import com.shedule.zyx.myshedule.utils.Constants.Companion.SCHEDULE
import com.shedule.zyx.myshedule.utils.Constants.Companion.TEACHERS
import com.shedule.zyx.myshedule.utils.Utils.Companion.getKeyByName
import com.shedule.zyx.myshedule.utils.filterNotNull
import rx.Observable
import java.util.*

/**
 * Created by bogdan on 16.09.16.
 */
class FirebaseWrapper(val ref: DatabaseReference, val prefs: AppPreference, val auth: FirebaseAuth) {

  companion object {
    val EVENTS = "events"
  }

  fun createTeacherRef() = facultyRef().child("teachers")

  private fun facultyRef(): DatabaseReference {
    return ref.child(BuildConfig.FIREBASE_URL).child(getKeyByName(prefs.getUniverName() ?: ""))
        .child(getKeyByName(prefs.getFacultyName() ?: ""))
  }

  fun createGroup(university: String, faculty: String, group: String): Observable<Boolean?> {
    createGroupRef(university, faculty, group).child(GROUP_CREATED).setValue(true)
    return RxFirebase.observe(createGroupRef(university, faculty, group).child(GROUP_CREATED))
        .map { it.getValue(Boolean::class.java) }
  }

  fun createGroupRef(university: String, faculty: String, group: String) = ref.child(getKeyByName(university)).child(getKeyByName(faculty)).child(getKeyByName(group))

  fun groupRef() = ref.child(getKeyByName(prefs.getUniverName() ?: ""))
      .child(getKeyByName(prefs.getFacultyName() ?: ""))
      .child(getKeyByName(prefs.getGroupName() ?: ""))

  private fun chainToAdminRef() = groupRef().child(ADMIN)

  fun createAccount(university: String, faculty: String, group: String): Observable<List<Schedule>?> {
    return Observable.create { subscription ->
      auth.signInAnonymously().addOnCompleteListener { task ->
        if (task.isSuccessful) {
          createGroup(university, faculty, group).subscribe({ done ->
            if (!prefs.isLogin())
              getSchedule(university, faculty, group).subscribe({ schedule ->
                  if (done ?: false) {
                    getChangesCount(createGroupRef(university, faculty, group))
                      .subscribe({ prefs.saveChangesCount(it) }, {})
                    subscription.onNext(schedule)
                    subscription.onCompleted()
                  }
              }, {
                if (it.message == EMPTY_DATA) {
                  subscription.onNext(null)
                  subscription.onCompleted()
                }
              })
            else {
              subscription.onNext(null)
              subscription.onCompleted()
            }
          }, {})
        } else subscription.onError(task.exception)
      }
    }
  }

  fun logOut(): Observable<Void> {
    return Observable.create {
      auth.currentUser?.delete()?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
          it.onNext(null)
          if (prefs.getAdminRight())
            groupRef().child(ADMIN).removeValue()
          it.onCompleted()
        } else it.onError(task.exception)
      }
    }
  }

  fun removeAdmin(): Observable<Boolean> {
    return RxFirebase.observe(groupRef().child(ADMIN)).flatMap {
      if (it.key != null) {
        groupRef().child(ADMIN).removeValue()
        Observable.just(true)
      } else Observable.just(false)
    }
  }

  fun getGroups(faculty: String, university: String): Observable<List<String>?> {
    return RxFirebase.observe(ref.child(getKeyByName(university)).child(getKeyByName(faculty))).map { it.children?.map { it.key }?.filter { it != TEACHERS } }
  }

  fun getFaculty(university: String): Observable<List<String>?> {
    return RxFirebase.observe(ref.child(getKeyByName(university))).map { it.children?.map { it.key } }
  }

  fun getUniversity(): Observable<List<String>?> {
    return RxFirebase.observe(ref).map { it.children?.map { it.key } }
  }

  fun getSubjects() = getTeachers().map { it?.map { it.lessonName.toString() } }

  fun getTeachers(): Observable<List<Teacher>?> {
    return RxFirebase.observe(createTeacherRef())
        .filterNotNull()
        .map { it.children.map { it.getValue(Teacher::class.java) } }
  }

  fun getSchedule(university: String = "",
                  faculty: String = "",
                  group: String = ""): Observable<List<Schedule>?> {

    val reference: DatabaseReference
    if (!university.isNullOrEmpty() && !faculty.isNullOrEmpty() && !group.isNullOrEmpty())
      reference = createGroupRef(university, faculty, group).child(SCHEDULE)
    else reference = groupRef().child(SCHEDULE)

    return RxFirebase.observe(reference)
        .filterNotNull().map { it.children.map { it.getValue(Schedule::class.java) } }
  }

  fun pushSchedule(list: List<Schedule>, change: Int): Observable<Boolean> {
    groupRef().child(SCHEDULE).setValue(list)
    groupRef().child(CHANGES_COUNT).setValue(change)
    return RxFirebase.observe(groupRef())
        .flatMap {
          if (it.children != null) Observable.just(true)
          else Observable.just(false)
        }
  }

  fun getChangesCount(reference: DatabaseReference? = null): Observable<Int> {
    return RxFirebase.observe(reference?.child(CHANGES_COUNT) ?: groupRef().child(CHANGES_COUNT))
        .filterNotNull()
        .map { it.getValue(Int::class.java) }
  }

  fun pushAdmin(): Observable<String> {

    return RxFirebase.observe(groupRef())
        .flatMap {
          var isExist = false
          it.children?.map { if (it.key == ADMIN) isExist = true }
          if (isExist) {
            Observable.just(ADMIN_IS_EXISTS)
          } else {
            chainToAdminRef().setValue(auth.currentUser?.uid)
            Observable.just(it.value.toString())
          }
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
                      if (DEBOUG_ENABLED)
                        report(it)
                      subscriber.onError(it)
                    })
              }
            }, {
              if (DEBOUG_ENABLED)
                report(it)
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
                if (DEBOUG_ENABLED)
                  report(it)
                subscriber.onError(it)
              })
        }
      }, {
        if (DEBOUG_ENABLED)
          report(it)
        subscriber.onError(it)
      })
    }
  }

  fun pushRating(rating: Double, teacherName: String) {
    createTeacherRef().child(getKeyByName(teacherName)).child(RATINGS).child(auth.currentUser?.uid).setValue(rating)
  }

  fun getEvents(cityName: String) = RxFirebase.observe(ref.child(EVENTS).child(getKeyByName(cityName)))
      .map { listOf(it.getValue(Event::class.java)).map { it } }
}