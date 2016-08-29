package com.shedule.zyx.myshedule.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.adapters.CategoriesPagerAdapter
import kotlinx.android.synthetic.main.dialog_view.*
import kotlinx.android.synthetic.main.done_buttons.*
import org.jetbrains.anko.onClick

/**
 * Created by Alexander on 20.08.2016.
 */
class BluetoothDialog : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

    val dialog = super.onCreateDialog(savedInstanceState)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    return dialog
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
      inflater!!.inflate(R.layout.dialog_view, container, false)

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val adapter = CategoriesPagerAdapter(childFragmentManager)
    categories_pager.adapter = adapter

    btn_ok.onClick { dismiss() }
  }
}