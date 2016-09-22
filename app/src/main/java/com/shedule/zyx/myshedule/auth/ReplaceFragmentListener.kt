package app.voter.xyz.auth

import android.support.v4.app.Fragment

/**
 * Created by bogdan on 13.09.16.
 */
interface ReplaceFragmentListener {
  fun changeVisibleFragment(title: String, fragment: Fragment?)
}