package app.voter.xyz.comments

/**
 * Created by bogdan on 23.08.16.
 */
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.comment_item_layout.view.*
import kotlinx.android.synthetic.main.schedule_dialog_layout.view.*
import org.jetbrains.anko.onClick


/**
 * Created by bogdan on 23.08.16.
 */
class CommentsAdapter(val context: Context, val ref: DatabaseReference, val userId: String, val listener: OnCommentClickListener) : FirebaseRecyclerAdapter<CommentFirebase, CommentsAdapter.ViewHolder>(CommentFirebase::class.java,
    R.layout.comment_item_layout, ViewHolder::class.java, ref) {

  override fun populateViewHolder(viewHolder: ViewHolder, model: CommentFirebase, position: Int) {

    viewHolder.view.like_image.isSelected = false
    model.likes.filterValues { it.equals(userId) }.map { viewHolder.view.like_image.isSelected = true }
    viewHolder.view.like_count.text = "${model.likes.size}"
    viewHolder.view.text.text = model.text
    if (model.datetime.isNotEmpty())
      viewHolder.view.time.text = DateUtils
          .getRelativeDateTimeString(context, model.datetime.toLong(), DateUtils.SECOND_IN_MILLIS, 3 * DateUtils.YEAR_IN_MILLIS, 0)
          .split(",").first()
    viewHolder.view.onClick { listener.onCommentClicked(userId, model.likes.filterValues { it.equals(userId) }.map { it.key }.firstOrNull(), position, null) }
    viewHolder.view.container.removeAllViews()
    model.replies.map { data ->
      val view = CommentView(context)
      view.onClick { listener.onCommentClicked(data.value.user_id, data.value.likes.filterValues { it.equals(userId) }.map { it.key }.firstOrNull(), position, data.key) }
      view.setData(data.value)
      viewHolder.view.container.addView(view)
    }
  }

  class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

  interface OnCommentClickListener {
    fun onCommentClicked(userId: String, keyLike: String?, position: Int, replayId: String?)
  }
}