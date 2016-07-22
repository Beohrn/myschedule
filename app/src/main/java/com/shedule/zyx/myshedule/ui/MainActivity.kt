package com.shedule.zyx.myshedule.ui

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_navigation.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        setSupportActionBar(main_toolbar)
        setupDataForViewPager(main_viewpager)
        main_tabs.setupWithViewPager(main_viewpager)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, main_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout?.setDrawerListener(toggle)
        toggle.syncState()
        nav_view?.setNavigationItemSelectedListener(this)
    }

    private fun setupDataForViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        //todo implement this uses great solution for adapter current implementation is not cool
        adapter.addFragment(ScheduleFragment(), "Monday")
        adapter.addFragment(ScheduleFragment(), "Monday")
        adapter.addFragment(ScheduleFragment(), "Monday")
        viewPager.adapter = adapter
    }

    override fun onBackPressed() {
        if (drawer_layout?.isDrawerOpen(GravityCompat.START)!!) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.calendar_menu, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem?): Boolean {

        //todo implement this
        when (item?.itemId) {
        }

        drawer_layout?.closeDrawer(GravityCompat.START)
        return true
    }
}
