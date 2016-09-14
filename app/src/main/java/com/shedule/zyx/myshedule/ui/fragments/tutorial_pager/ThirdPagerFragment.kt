package com.shedule.zyx.myshedule.ui.fragments.tutorial_pager

import com.cleveroad.slidingtutorial.Direction
import com.cleveroad.slidingtutorial.PageFragment
import com.cleveroad.slidingtutorial.TransformItem
import com.shedule.zyx.myshedule.R

/**
 * Created by alexkowlew on 14.09.2016.
 */
class ThirdPagerFragment: PageFragment() {
  override fun getLayoutResId() = R.layout.fragment_page_third

  override fun getTransformItems() = arrayOf(TransformItem.create(R.id.ivFirstImage, Direction.RIGHT_TO_LEFT, 0.2f),
      TransformItem.create(R.id.ivSecondImage, Direction.LEFT_TO_RIGHT, 0.06f),
      TransformItem.create(R.id.ivThirdImage, Direction.RIGHT_TO_LEFT, 0.08f),
      TransformItem.create(R.id.ivFourthImage, Direction.LEFT_TO_RIGHT, 0.1f),
      TransformItem.create(R.id.ivFifthImage, Direction.LEFT_TO_RIGHT, 0.03f),
      TransformItem.create(R.id.ivSixthImage, Direction.LEFT_TO_RIGHT, 0.09f),
      TransformItem.create(R.id.ivSeventhImage, Direction.LEFT_TO_RIGHT, 0.14f))
}