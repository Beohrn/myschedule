package com.shedule.zyx.myshedule.adapters




import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.shedule.zyx.myshedule.ui.BondedDevicesFragment
import com.shedule.zyx.myshedule.ui.NearbyDevicesFragment

/**
 * Created by Alexander on 20.08.2016.
 */
class CategoriesPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return BondedDevicesFragment()
            1 -> return NearbyDevicesFragment()
        }
        return null!!
    }
}