package com.shedule.zyx.myshedule.events

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.graphics.Color.WHITE
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.ui.activities.BaseActivity
import com.shedule.zyx.myshedule.utils.Utils.Companion.isOnline
import com.shedule.zyx.myshedule.utils.toMainThread
import com.shedule.zyx.myshedule.widget.EventView.LinkListener
import kotlinx.android.synthetic.main.event_activity.*
import org.jetbrains.anko.onItemSelectedListener

/**
 * Created by bogdan on 08.10.16.
 */
class EventActivity : BaseActivity(), LinkListener {

  lateinit var adapter: EventsRecycleAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.event_activity)

    toggleEmptyEvents(false)
    ev_no_connection.visibility = View.GONE

    setSupportActionBar(events_toolbar)
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.title = ""
    recycle_view.layoutManager = LinearLayoutManager(this)
    events_toolbar.setTitleTextColor(WHITE)

    spinner.onItemSelectedListener {
      onItemSelected { adapterView, view, i, l ->
        with((adapterView?.getChildAt(0) as TextView)) {
          setTextColor(resources.getColor(R.color.white))
          textSize = 20.toFloat()

          showProgressDialog(getString(R.string.load))
          if (isOnline(this@EventActivity))
            firebaseWrapper.getEvents(resources.getStringArray(R.array.city_ukraine)[i]).toMainThread()
                .subscribe({
                  hideProgressDialog()
                  if (it != null) {
                    if (it.size != 0) toggleEmptyEvents(false)
                    else toggleEmptyEvents(true)
                    adapter = EventsRecycleAdapter(this@EventActivity, it, this@EventActivity)
                    recycle_view.adapter = adapter
                  } else toggleEmptyEvents(true)
                }, {
                  hideProgressDialog()
                  toggleEmptyEvents(true)
                })
          else noInternetConnection()
        }
      }
    }
  }

  override fun openLink(link: String) = startActivity(Intent(ACTION_VIEW, Uri.parse(link)))

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (item?.itemId == android.R.id.home) finish()
    return super.onOptionsItemSelected(item)
  }

  fun toggleEmptyEvents(isEmpty: Boolean) {
    recycle_view.visibility = if (isEmpty) View.GONE else View.VISIBLE
    no_events_yet.visibility = if (isEmpty) View.VISIBLE else View.GONE
  }

  fun noInternetConnection() {
    hideProgressDialog()
    recycle_view.visibility = View.GONE
    no_events_yet.visibility = View.GONE
    ev_no_connection.visibility = View.VISIBLE
  }
}