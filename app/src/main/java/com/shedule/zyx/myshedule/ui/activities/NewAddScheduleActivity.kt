package com.shedule.zyx.myshedule.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.adapters.RepeatDialogPagerAdapter
import kotlinx.android.synthetic.main.add_schedule_screen.*
import kotlinx.android.synthetic.main.new_add_schedule_screen.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.include
import org.jetbrains.anko.onClick

/**
 * Created by alexkowlew on 08.09.2016.
 */
class NewAddScheduleActivity: AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.new_add_schedule_screen)
    setSupportActionBar(new_add_schedule_toolbar)
    supportActionBar?.title = "Add Schedule"
    new_add_schedule_toolbar.setTitleTextColor(Color.WHITE)

    type_and_number_of_lesson.onClick {
      val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_new)
      bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    repeat_dates.onClick {
      val dialog = DatesRepeatDialog()
      dialog.show(supportFragmentManager, "")


  }
}