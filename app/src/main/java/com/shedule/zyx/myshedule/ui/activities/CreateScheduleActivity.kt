package com.shedule.zyx.myshedule.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import com.shedule.zyx.myshedule.R
import kotlinx.android.synthetic.main.create_schedule_layout.*
import org.jetbrains.anko.onClick

/**
 * Created by bogdan on 27.07.16.
 */
class CreateScheduleActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.create_schedule_layout)

    one.onClick { (it as CardView).cardElevation = 10f }

  }

}