package com.shedule.zyx.myshedule.comments

import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.shedule.zyx.myshedule.RxFirebase
import app.voter.xyz.comments.Comment
import app.voter.xyz.comments.CommentsAdapter
import com.google.firebase.database.DatabaseReference
import com.shedule.zyx.myshedule.BuildConfig
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ui.activities.BaseActivity
import com.shedule.zyx.myshedule.utils.Constants
import com.shedule.zyx.myshedule.utils.Constants.Companion.COMMENTS
import com.shedule.zyx.myshedule.utils.Constants.Companion.LIKES
import com.shedule.zyx.myshedule.utils.Constants.Companion.REPLIES
import com.shedule.zyx.myshedule.utils.Utils.Companion.getKeyByName
import kotlinx.android.synthetic.main.discussion_activity_layout.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.onClick
import org.jetbrains.anko.selector
import java.util.*

class DiscussionActivity : BaseActivity(), CommentsAdapter.OnCommentClickListener {

  var whichId = ""

  var isReplying = false
  var replyCommentKey: DatabaseReference? = null

  lateinit var ref: DatabaseReference
  lateinit var layoutManager: LinearLayoutManager

  companion object {
    val TEACHER_REQUEST = "teacher_name"
  }

  var recycleAdapter: CommentsAdapter? = null
  val comments = arrayListOf<String>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.discussion_activity_layout)

    whichId = getKeyByName(intent.getStringExtra(TEACHER_REQUEST))

    ref = firebaseRef.child(BuildConfig.FIREBASE_URL).child(getKeyByName(prefs.getUniverName()))
        .child(getKeyByName(prefs.getFacultyName()))
        .child(Constants.TEACHERS)
        .child(whichId)
        .child(COMMENTS)

    RxFirebase.observeChildAdded(ref).subscribe({
      recycleAdapter?.let { discus_recycleview.smoothScrollToPosition(recycleAdapter!!.itemCount) }
    }, {})

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      discussion_toolbar.elevation = dip(10).toFloat()
    }

    setSupportActionBar(discussion_toolbar)
    supportActionBar?.title = getString(R.string.comments)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.setHomeButtonEnabled(true)

    discus_recycleview.layoutManager = LinearLayoutManager(this@DiscussionActivity)
    recycleAdapter = CommentsAdapter(this@DiscussionActivity, ref, deviceToken, this@DiscussionActivity)
    discus_recycleview.adapter = recycleAdapter
    discus_recycleview.setHasFixedSize(true)
    layoutManager = LinearLayoutManager(this)

    post_btn.onClick {
      onPostButtonClick()
    }
  }

  fun onPostButtonClick() {
    val text = commentET.text.toString().trim()

    if (!text.isNullOrBlank()) {
      val comment = Comment(deviceToken, text, getTimeCommentPost())

      if (isReplying) {
        replyCommentKey?.child(REPLIES)?.push()?.setValue(comment)
      } else {
        ref.push().setValue(comment)
      }

      toggleReply(false)
    }
  }

  fun toggleReply(isReply: Boolean) {
    isReplying = isReply
    if (!isReply) commentET.setText("")
    post_btn?.text = if (isReply) getString(R.string.reply) else getString(R.string.send_comment)
    commentET.hint = if (isReply) getString(R.string.reply) else getString(R.string.send_comment_to)
  }

  override fun onCommentClicked(commentRef: DatabaseReference, comment: Comment) {
    val likeId = comment.isLikedByUser(deviceToken)
    val isLiked = likeId != null
    val likeText = if (!isLiked) getString(R.string.like) else getString(R.string.dislike)

    selector(items = listOf(likeText, getString(R.string.reply))) {
      when (it) {
        0 -> if (isLiked) removeLike(commentRef, likeId) else pushLike(commentRef)
        else -> replyComment(commentRef)
      }
    }
  }

  override fun onReplyClicked(commentRef: DatabaseReference, replyComment: Comment) {
    val likeId = replyComment.isLikedByUser(deviceToken)
    if (likeId != null) removeLike(commentRef, likeId) else pushLike(commentRef)
  }

  private fun pushLike(commentRef: DatabaseReference) {
    commentRef.child(LIKES).push().setValue(deviceToken)
  }

  private fun removeLike(commentRef: DatabaseReference, likeId: String?) {
    commentRef.child(LIKES).child(likeId).removeValue()
  }

  private fun replyComment(commentKey: DatabaseReference) {
    replyCommentKey = commentKey
    toggleReply(true)
  }

  fun getTimeCommentPost() = "${GregorianCalendar().timeInMillis}"

  override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
    if (menuItem.itemId === android.R.id.home) {
      finish()
    }
    return super.onOptionsItemSelected(menuItem)
  }
}