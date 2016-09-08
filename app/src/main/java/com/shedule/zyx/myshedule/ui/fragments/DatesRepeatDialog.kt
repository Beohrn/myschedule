package com.shedule.zyx.myshedule.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.adapters.RepeatDialogPagerAdapter
import kotlinx.android.synthetic.main.date_repeat_dialog.*
import kotlinx.android.synthetic.main.date_repeat_pager.*
import org.jetbrains.anko.find

/**
 * Created by alexkowlew on 08.09.2016.
 */
class DatesRepeatDialog: DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    return dialog
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
      inflater?.inflate(R.layout.date_repeat_dialog, container, false)

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view?.find<Toolbar>(R.id.repeat_dialog_toolbar)
    toolbar?.title = "Повторения"
    toolbar?.setTitleTextColor(Color.WHITE)
    val adapter = RepeatDialogPagerAdapter(childFragmentManager)
    date_repeat_pager.adapter = adapter
    repeat_dialog_tabs.setupWithViewPager(date_repeat_pager)
  }
}