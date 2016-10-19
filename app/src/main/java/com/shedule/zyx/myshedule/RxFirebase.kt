package com.shedule.zyx.myshedule


import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import com.shedule.zyx.myshedule.utils.Constants.Companion.EMPTY_DATA
import rx.Observable
import rx.functions.Func1
import rx.subscriptions.Subscriptions

object RxFirebase {
  fun observeChildren(ref: DatabaseReference): Observable<FirebaseChildEvent> {
    return Observable.create<FirebaseChildEvent> { subscriber ->
      val listener = ref.addChildEventListener(object : ChildEventListener {
        override fun onChildMoved(dataSnapshot: DataSnapshot?, prevName: String?) {
          subscriber.onNext(FirebaseChildEvent(dataSnapshot, EventType.CHILD_MOVED, prevName))
        }

        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
          subscriber.onNext(FirebaseChildEvent(p0, EventType.CHILD_CHANGED, p1))
        }

        override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
          subscriber.onNext(FirebaseChildEvent(p0, EventType.CHILD_ADDED, p1))
        }

        override fun onChildRemoved(p0: DataSnapshot?) {
          subscriber.onNext(FirebaseChildEvent(p0, EventType.CHILD_REMOVED, null))
        }

        override fun onCancelled(p0: DatabaseError?) {
        }
      })

      subscriber.add(Subscriptions.create { ref.removeEventListener(listener) })
    }
  }

  private fun makeEventFilter(eventType: EventType): Func1<FirebaseChildEvent, Boolean> {
    return Func1 { firebaseChildEvent -> eventType == firebaseChildEvent.eventType }
  }

  fun observeChildAdded(ref: DatabaseReference): Observable<FirebaseChildEvent> {
    return observeChildren(ref).filter(makeEventFilter(EventType.CHILD_ADDED))
  }

  fun observeChildChanged(ref: DatabaseReference): Observable<FirebaseChildEvent> {
    return observeChildren(ref).filter(makeEventFilter(EventType.CHILD_CHANGED))
  }

  fun observeChildMoved(ref: DatabaseReference): Observable<FirebaseChildEvent> {
    return observeChildren(ref).filter(makeEventFilter(EventType.CHILD_MOVED))
  }

  fun observeChildRemoved(ref: DatabaseReference): Observable<FirebaseChildEvent> {
    return observeChildren(ref).filter(makeEventFilter(EventType.CHILD_REMOVED))
  }

  fun observe(ref: DatabaseReference): Observable<DataSnapshot> {

    return Observable.create { subscriber ->
      val listener = ref.addValueEventListener(object : ValueEventListener {
        override fun onCancelled(error: DatabaseError?) {
          subscriber.onError(FirebaseException(error!!.message))
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
          if (dataSnapshot.value == null) subscriber.onError(Exception(EMPTY_DATA))
          else subscriber.onNext(dataSnapshot)
        }
      })

      // When the subscription is cancelled, remove the listener
      subscriber.add(Subscriptions.create { ref.removeEventListener(listener) })
    }
  }

  fun snapshot(ref: Query): Observable<DataSnapshot> {

    return Observable.create { subscriber ->
      ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {
          subscriber.onError(FirebaseException(p0!!.message))
        }

        override fun onDataChange(p0: DataSnapshot?) {
          subscriber.onNext(p0)
          subscriber.onCompleted()
        }
      })
    }
  }


  enum class EventType {
    CHILD_CHANGED, CHILD_REMOVED, CHILD_MOVED, CHILD_ADDED
  }

  class FirebaseChildEvent(var snapshot: DataSnapshot?, var eventType: EventType, var prevName: String?)
}
