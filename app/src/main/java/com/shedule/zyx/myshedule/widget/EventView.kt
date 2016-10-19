package com.shedule.zyx.myshedule.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import at.blogc.android.views.ExpandableTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.shedule.zyx.myshedule.R
import com.shedule.zyx.myshedule.events.Event
import kotlinx.android.synthetic.main.event_list_item.view.*
import org.jetbrains.anko.onClick

/**
 * Created by bogdan on 09.10.16.
 */
class EventView : FrameLayout {

  var listener: LinkListener? = null

  constructor(context: Context?) : super(context) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    init(context)
  }

  fun init(context: Context?) = inflate(context, R.layout.event_list_item, this)

  fun setData(event: Event) {
    university_name.text = event.universityName
    title.text = event.title
    description.text = event.discription
    progressBar.visibility = View.VISIBLE
    link.onClick { listener?.let { listener -> event.link?.let { listener.openLink(it) } } }

    description.onClick {
      var view = (it as ExpandableTextView)
      if (view.isExpanded) view.collapse() else view.expand()
    }

    Glide.with(context).load(event.image).diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(image_poster)

    Glide.with(context).load(event.imageLogo).diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(logo)

    Glide.with(context).load(event.image).listener(object : RequestListener<String, GlideDrawable> {
      override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?,
                                   isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
        progressBar.visibility = View.GONE
        return false

      }

      override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
        progressBar.visibility = View.GONE
        return false
      }

    }).diskCacheStrategy(DiskCacheStrategy.ALL).into(image_poster)
  }

  interface LinkListener {
    fun openLink(link: String)
  }
}