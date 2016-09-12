package app.voter.xyz.comments

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import org.jetbrains.anko.*

/**
 * Created on 7/24/16.
 */
class CommentView : FrameLayout {

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

  fun setData(replayComment: CommentFirebase) {
    removeAllViews()
    with(this) {

      verticalLayout {
        linearLayout {
          lparams { setPadding(0, dip(16), 0, 0) }

          verticalLayout {
            textView("") {
              typeface = Typeface.DEFAULT_BOLD
              textColor = Color.BLACK
              rightPadding = dip(16)
            }.lparams(width = matchParent)
            textView(replayComment.text) { textColor = Color.BLACK }

            linearLayout {
              gravity = Gravity.CENTER_VERTICAL
              val timeText = DateUtils
                  .getRelativeDateTimeString(context, replayComment.datetime.toLong(), DateUtils.SECOND_IN_MILLIS, 3 * DateUtils.YEAR_IN_MILLIS, 0)
                  .split(",").first()
              textView(timeText) {
                textSize = 12f
              }
              imageView(R.drawable.thumbs_up) {
                setImageResource(R.drawable.thumbs_up)
                isSelected = if (replayComment.likes.filterValues { it.equals(replayComment.user_id) }.map { true }.firstOrNull() == null) false else true
              }.lparams(dip(10)){
                leftMargin = dip(10)
                rightMargin = dip(10)
              }
              textView(replayComment.likes.size.toString()) {
                textSize = 12f
              }
            }.lparams {
              topMargin = dip(5)
            }

            view { backgroundColor = context.resources.getColor(R.color.material_gray) }.lparams(matchParent, dip(1)) {
              topMargin = dip(16)
            }
          }.lparams(matchParent)
        }
      }
    }
  }
}