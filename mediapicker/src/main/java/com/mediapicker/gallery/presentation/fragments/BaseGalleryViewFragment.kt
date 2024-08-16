package com.mediapicker.gallery.presentation.fragments

import android.app.Activity
import com.mediapicker.gallery.presentation.activity.GalleryActionListener

abstract class BaseGalleryViewFragment : BaseFragment() {

    protected var galleryActionListener: GalleryActionListener? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (activity is GalleryActionListener) {
            galleryActionListener = activity
        }
    }

    override fun setUpViews() {}

    open fun onActionButtonClick() {}

    override fun onBackPressed() {
        activity?.onBackPressed()
    }
}