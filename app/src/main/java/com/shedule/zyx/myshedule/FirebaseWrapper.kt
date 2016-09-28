package com.shedule.zyx.myshedule

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.shedule.zyx.myshedule.config.AppPreference
import com.shedule.zyx.myshedule.models.Teacher
import com.shedule.zyx.myshedule.utils.Constants.Companion.RATINGS
import com.shedule.zyx.myshedule.utils.Utils.Companion.getKeyByName
import java.util.*

/**
 * Created by bogdan on 16.09.16.
 */
class FirebaseWrapper(val ref: DatabaseReference, val prefs: AppPreference, val token: String) {

  var teachersRef = createTeacherRef()

  private fun createTeacherRef(): DatabaseReference {
    return ref.child(getKeyByName(prefs.getUniverName() ?: ""))
        .child(getKeyByName(prefs.getFacultyName() ?: ""))
        .child("teachers")
  }

  fun updateTeacherRef() {
    teachersRef = createTeacherRef()
  }

  fun pushTeacher(teacher: Teacher) {
    teachersRef.child(getKeyByName(teacher.teacherName)).addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onCancelled(p0: DatabaseError?) {
      }

      override fun onDataChange(data: DataSnapshot?) {
        if (data?.value == null) {
          teachersRef.child(getKeyByName(teacher.teacherName)).setValue(teacher)
        } else {
          (data?.value as HashMap<String, Any>).keys.toList()
              .filter { it.equals(getKeyByName(teacher.teacherName)) }
              .firstOrNull()?.let {
            teachersRef.child(it)
                .updateChildren(mapOf(Pair("", ObjectMapper().convertValue(teacher, Map::class.java))))
          }
        }
      }
    })
  }

  fun pushRating(rating: Double, teacherName: String) {
    teachersRef.child(getKeyByName(teacherName)).child(RATINGS).child(token).setValue(rating)
  }
}