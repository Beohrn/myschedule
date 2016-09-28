package app.voter.xyz.comments

/**
 * Created by bogdan on 23.08.16.
 */
import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.utils.Constants
import kotlinx.android.synthetic.main.comment_item_layout.view.*
import org.jetbrains.anko.onClick

/**
 * Created by bogdan on 23.08.16.
 */
class CommentsAdapter(val context: Context, val ref: DatabaseReference, val userId: String, val listener: OnCommentClickListener) : FirebaseRecyclerAdapter<Comment, CommentsAdapter.ViewHolder>(Comment::class.java,
    R.layout.comment_adapter_item, ViewHolder::class.java, ref) {

  override fun populateViewHolder(viewHolder: ViewHolder, model: Comment, position: Int) {

    val commentView = (viewHolder.view as CommentView)
    commentView.setData(model, userId)
    commentView.text.typeface = Typeface.DEFAULT_BOLD
    commentView.text.textSize = 14f
    viewHolder.view.onClick { listener.onCommentClicked(getRef(position), model) }

    commentView.container.removeAllViews()
    model.replies.map { data ->
      val view = CommentView(context)
      view.onClick {
        listener.onReplyClicked(getRef(position).child(Constants.REPLIES).child(data.key), data.value)
      }
      view.setData(data.value, userId)
      commentView.container.addView(view)
    }
  }

  class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

  interface OnCommentClickListener {
    fun onCommentClicked(commentRef: DatabaseReference, comment: Comment)
    fun onReplyClicked(commentRef: DatabaseReference, reply: Comment)
  }
}