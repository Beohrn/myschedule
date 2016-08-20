package com.shedule.zyx.myshedule.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.schedule_item_layout.view.*
import org.jetbrains.anko.onClick

/**
 * Created by bogdan on 31.07.16.
 */
class ScheduleItemView : FrameLayout {
  lateinit var mAnimator: ValueAnimator
  var s = false

  constructor(context: Context?) : super(context) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context)
  }

  fun init(context: Context?) {
    inflate(context, R.layout.schedule_item_layout, this)

    onClick {
      if (!s) {
//        val anim1 = ObjectAnimator.ofFloat(this, "scaleX", 0.97f)
//        val anim2 = ObjectAnimator.ofFloat(this, "scaleY", 0.95f)
        val anim = ObjectAnimator.ofFloat(this, "translationZ", 0f, 200f)

        val set = AnimatorSet().setDuration(300)
        set.play(anim)
        set.start()

        s = true
        if (expand_container.visibility === View.GONE) expand() else collapse()
      } else {
//        val anim1 = ObjectAnimator.ofFloat(this, "scaleX", 1f)
//        val anim2 = ObjectAnimator.ofFloat(this, "scaleY", 1f)
        val anim = ObjectAnimator.ofFloat(this, "translationZ", 200f, 0f)

        val set = AnimatorSet().setDuration(200)
        set.play(anim)
        set.start()
        s = false
        if (expand_container.visibility === View.GONE) expand() else collapse()
      }
    }

    expand_container.post {
      mAnimator = slideAnimator(0, expand_container.height)
      expand_container.visibility = View.GONE
    }
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