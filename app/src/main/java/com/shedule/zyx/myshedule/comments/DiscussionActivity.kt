package app.voter.xyz.comments

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import app.voter.xyz.RxFirebase
import app.voter.xyz.utils.Constants.Companion.COMMENTS
import app.voter.xyz.utils.Constants.Companion.REPLIES
import com.google.firebase.database.DatabaseReference
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import kotlinx.android.synthetic.main.discussion_activity_layout.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.onClick
import org.jetbrains.anko.selector
import java.util.*
import javax.inject.Inject

class DiscussionActivity : AppCompatActivity(), CommentsAdapter.OnCommentClickListener {

  @Inject
  lateinit var firebaseRef: DatabaseReference

  @Inject
  lateinit var deviceToken: String

  val whichId = "111"
  val discussRef = "discussion"

  lateinit var ref: DatabaseReference
  lateinit var layoutManager: LinearLayoutManager


  var recycleAdapter: CommentsAdapter? = null
  val comments = arrayListOf<String>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.discussion_activity_layout)
    ScheduleApplication.getComponent().inject(this)

    ref = firebaseRef.child(discussRef).child(whichId).child(COMMENTS)

    RxFirebase.observeChildAdded(ref).subscribe({
      recycleAdapter?.let { discus_recycleview.smoothScrollToPosition(recycleAdapter!!.itemCount) }
    }, {})

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      discussion_toolbar.elevation = dip(10).toFloat()
    }

    setSupportActionBar(discussion_toolbar)
    supportActionBar?.title = "Коментарии"
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.setHomeButtonEnabled(true)

    discus_recycleview.layoutManager = LinearLayoutManager(this@DiscussionActivity)
    recycleAdapter = CommentsAdapter(this@DiscussionActivity, ref, deviceToken, this@DiscussionActivity)
    discus_recycleview.adapter = recycleAdapter
    discus_recycleview.setHasFixedSize(true)
    layoutManager = LinearLayoutManager(this)

    post_btn.onClick {
      postComment(commentET?.text.toString())
      commentET?.setText("")
    }
  }

  override fun onCommentClicked(userId: String, keyLike: String?, position: Int, replayId: String?) {

    if (replayId.isNullOrEmpty()) {
      if (keyLike.isNullOrEmpty()) selector(items = listOf("Like", "Reply")) {
        if (it == 0) {
          pushLike(deviceToken, firebaseRef.child(discussRef).child(whichId)
              .child(COMMENTS).child(recycleAdapter?.getRef(position)?.key))
        } else replayComment(position)
      } else selector(items = listOf("Dislike", "Reply")) {
        if (it == 0) {
          removeLike(keyLike, firebaseRef.child(discussRef).child(whichId)
              .child(COMMENTS).child(recycleAdapter?.getRef(position)?.key))
        } else replayComment(position)
      }
    } else {
      if (keyLike.isNullOrEmpty()) selector(items = listOf("Like", "Reply")) {
        if (it == 0) {
          pushLike(deviceToken, firebaseRef.child(discussRef).child(whichId)
              .child(COMMENTS).child(recycleAdapter?.getRef(position)?.key).child(REPLIES).child(replayId))
        } else replayComment(position)
      } else selector(items = listOf("Dislike", "Reply")) {
        if (it == 0) {
          removeLike(keyLike, firebaseRef.child(discussRef).child(whichId)
              .child(COMMENTS).child(recycleAdapter?.getRef(position)?.key).child(REPLIES).child(replayId))
        } else replayComment(position)
      }
    }
  }

  private fun pushLike(deviceId: String, ref: DatabaseReference) = ref.child("likes").push().setValue(deviceId)

  private fun replayComment(position: Int) {
    if (commentET?.text.toString().isNotEmpty()) {
      firebaseRef.child(discussRef).child(whichId).child(COMMENTS).child(recycleAdapter?.getRef(position)?.key).child(REPLIES).push()
          .setValue(CommentFirebase(deviceToken, commentET?.text.toString(), getTimeCommentPost()))
      commentET?.setText("")
    }
  }

  private fun removeLike(keyLike: String?, ref: DatabaseReference) = ref.child("likes").child(keyLike).removeValue()

  fun postComment(text: String) {
    if (text.isNotEmpty())
      firebaseRef.child(discussRef).child(whichId).child(COMMENTS).push()
          .setValue(CommentFirebase(deviceToken, text, getTimeCommentPost()))
  }

  fun getTimeCommentPost() = "${GregorianCalendar().timeInMillis}"

  override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
    if (menuItem.itemId === android.R.id.home) {
      finish()
    }
    return super.onOptionsItemSelected(menuItem)
  }
}
