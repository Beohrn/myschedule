package com.shedule.zyx.myshedule.events

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.TextView
import com.shedule.zyx.myshedule.FirebaseWrapper
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ScheduleApplication
import com.shedule.zyx.myshedule.toMainThread
import com.shedule.zyx.myshedule.widget.EventView.LinkListener
import kotlinx.android.synthetic.main.event_activity.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.onItemSelectedListener
import javax.inject.Inject

/**
 * Created by bogdan on 08.10.16.
 */
class EventActivity : AppCompatActivity(), LinkListener {

  @Inject
  lateinit var firebaseWrapper: FirebaseWrapper

  lateinit var adapter: EventsRecycleAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.event_activity)
    ScheduleApplication.getComponent().inject(this)

    setSupportActionBar(events_toolbar)
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    recycle_view.layoutManager = LinearLayoutManager(this)

    spinner.onItemSelectedListener {
      onItemSelected { adapterView, view, i, l ->
        with((adapterView?.getChildAt(0) as TextView)) {
          setTextColor(resources.getColor(R.color.white))
          textSize = 20.toFloat()

          val dialog = indeterminateProgressDialog(getString(R.string.load))
          dialog.setCanceledOnTouchOutside(false)
          dialog.show()
          firebaseWrapper.getEvents(resources.getStringArray(R.array.city_ukraine)[i]).toMainThread()
              .subscribe({
                dialog.dismiss()
                adapter = EventsRecycleAdapter(this@EventActivity, it, this@EventActivity)
                recycle_view.adapter = adapter
              }, {
                dialog.dismiss()
              })
        }
      }
    }
  }

  override fun openLink(link: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (item?.itemId == android.R.id.home) finish()
    return super.onOptionsItemSelected(item)
  }
}