package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

/**
 * Created by alexkowlew on 22.09.2016.
 */
class FabNestedScrollBehavior() : FloatingActionButton.Behavior() {

  constructor(context: Context, attributes: AttributeSet) : this() { }

  override fun onNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton?, target: View?, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
    super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    if (dyConsumed > 0 && child?.visibility == View.VISIBLE)
      child?.hide()
    else if (dyConsumed < 0 && child?.visibility == View.GONE)
      child?.show()
  }

  override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton?, directTargetChild: View?, target: View?, nestedScrollAxes: Int): Boolean {
    return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
        super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes)
  }
}