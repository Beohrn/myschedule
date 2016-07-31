package com.shedule.zyx.myshedule.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.schedule_item.view.*

/**
 * Created by bogdan on 31.07.16.
 */
class ScheduleItemView : FrameLayout {
  lateinit var mAnimator: ValueAnimator

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
    inflate(context, R.layout.schedule_item, this)

    expand_container.post {
      mAnimator = slideAnimator(0, expand_container.height)
      expand_container.visibility = View.GONE
    }

    edit.setOnClickListener { if (expand_container.visibility === View.GONE) expand() else collapse() }
  }

  private fun expand() {
    expand_container.visibility = View.VISIBLE
    mAnimator.start()
  }

  private fun collapse() {
    val finalHeight = expand_container.height

    val mAnimator = slideAnimator(finalHeight, 0)

    mAnimator.addListener(object : Animator.AnimatorListener {
      override fun onAnimationEnd(animator: Animator) {
        expand_container.visibility = View.GONE
      }

      override fun onAnimationStart(animator: Animator) {
      }

      override fun onAnimationCancel(animator: Animator) {
      }

      override fun onAnimationRepeat(animator: Animator) {
      }
    })
    mAnimator.start()
  }

  private fun slideAnimator(start: Int, end: Int): ValueAnimator {
    val animator = ValueAnimator.ofInt(start, end)

    animator.addUpdateListener { valueAnimator ->

      val layoutParams = expand_container.layoutParams
      layoutParams.height = valueAnimator.animatedValue as Int
      expand_container.layoutParams = layoutParams
    }
    return animator
  }
}