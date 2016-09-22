package app.voter.xyz.comments

import android.content.Context
import android.text.format.DateUtils
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.comment_item_layout.view.*

/**
 * Created on 7/24/16.
 */
class CommentView : FrameLayout {

  constructor(context: Context?) : super(context) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    init(context)
  }

  fun init(context: Context?) {
    inflate(context, R.layout.comment_item_layout, this)
  }

  fun setData(comment: Comment, userId: String) {
    text.text = comment.text
    time.text = DateUtils
        .getRelativeDateTimeString(context, comment.datetime.toLong(), DateUtils.SECOND_IN_MILLIS, 3 * DateUtils.YEAR_IN_MILLIS, 0)
        .split(",").first()

    like_image.isSelected = comment.likes.values.contains(userId)
    like_count.text = comment.likes.size.toString()
  }
}