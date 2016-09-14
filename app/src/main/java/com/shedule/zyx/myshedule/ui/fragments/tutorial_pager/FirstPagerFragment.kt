package com.shedule.zyx.myshedule.ui.fragments.tutorial_pager

import com.cleveroad.slidingtutorial.Direction
import com.cleveroad.slidingtutorial.PageFragment
import com.cleveroad.slidingtutorial.TransformItem
import com.shedule.zyx.myshedule.R

/**
 * Created by alexkowlew on 14.09.2016.
 */
class FirstPagerFragment : PageFragment() {
  override fun getLayoutResId() = R.layout.fragment_page_first

  override fun getTransformItems() = arrayOf(TransformItem.create(R.id.ivFirstImage, Direction.LEFT_TO_RIGHT, 0.2f),
      TransformItem.create(R.id.ivSecondImage, Direction.RIGHT_TO_LEFT, 0.06f),
      TransformItem.create(R.id.ivThirdImage, Direction.LEFT_TO_RIGHT, 0.08f),
      TransformItem.create(R.id.ivFourthImage, Direction.RIGHT_TO_LEFT, 0.1f),
      TransformItem.create(R.id.ivFifthImage, Direction.RIGHT_TO_LEFT, 0.03f),
      TransformItem.create(R.id.ivSixthImage, Direction.RIGHT_TO_LEFT, 0.09f),
      TransformItem.create(R.id.ivSeventhImage, Direction.RIGHT_TO_LEFT, 0.14f),
      TransformItem.create(R.id.ivEighthImage, Direction.RIGHT_TO_LEFT, 0.07f))

}