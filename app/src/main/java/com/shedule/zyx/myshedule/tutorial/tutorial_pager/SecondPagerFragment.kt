package com.shedule.zyx.myshedule.tutorial.tutorial_pager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shedule.zyx.myshedule.R

/**
 * Created by alexkowlew on 14.09.2016.
 */
class SecondPagerFragment: Fragment() {

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater!!.inflate(R.layout.fragment_page_second, container, false)
  }
}