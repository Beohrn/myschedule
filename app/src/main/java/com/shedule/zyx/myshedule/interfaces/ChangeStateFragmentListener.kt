package com.shedule.zyx.myshedule.interfaces

/**
 * Created by bogdan on 22.07.16.
 */
interface ChangeStateFragmentListener {
  fun addListener(listener: DataChangeListener)
  fun removeListener(listener: DataChangeListener)
}