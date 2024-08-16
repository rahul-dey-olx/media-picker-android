package com.mediapicker.gallery.presentation.fragments

import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.R
import com.mediapicker.gallery.data.repositories.GalleryService
import com.mediapicker.gallery.domain.contract.OnItemClickListener
import com.mediapicker.gallery.domain.entity.PhotoAlbum
import com.mediapicker.gallery.presentation.adapters.GalleryFolderAdapter
import com.mediapicker.gallery.presentation.utils.ItemDecorationAlbumColumns
import com.mediapicker.gallery.presentation.utils.getFragmentScopedViewModel
import com.mediapicker.gallery.presentation.viewmodels.LoadAlbumViewModel

class FolderViewFragment : BaseGalleryViewFragment(), OnItemClickListener<PhotoAlbum> {

    private val loadAlbumViewModel: LoadAlbumViewModel by lazy {
        getFragmentScopedViewModel { LoadAlbumViewModel(GalleryService(Gallery.getApp())) }
    }

    override fun getScreenTitle() = getString(R.string.oss_title_folder_fragment)

    private lateinit var adapter: GalleryFolderAdapter
    private lateinit var folderRV: RecyclerView
    private lateinit var actionButton: AppCompatButton

    private fun setAlbumData(setOfAlbum: HashSet<PhotoAlbum>) {
        adapter.apply {
            var albumList = mutableListOf<PhotoAlbum>().apply { this.addAll(setOfAlbum) }
            this.listOfFolders = albumList.sortedBy { it.name }
            notifyDataSetChanged()
        }
    }

    override fun getLayoutId() = R.layout.oss_fragment_folder_view

    override fun setUpViews() {
        super.setUpViews()
        adapter = GalleryFolderAdapter(
            requireContext(),
            listOfFolders = emptyList(),
            onItemClickListener = this
        )

        folderRV = childView.findViewById(R.id.folderRV)
        actionButton = childView.findViewById(R.id.actionButton)

        folderRV.apply {
            this.addItemDecoration(
                ItemDecorationAlbumColumns(
                    resources.getDimensionPixelSize(R.dimen.module_base),
                    COLUMNS_COUNT
                )
            )
            this.layoutManager = GridLayoutManager(this@FolderViewFragment.activity, COLUMNS_COUNT)
            this.adapter = this@FolderViewFragment.adapter
        }

        actionButton.isSelected = true

        if (Gallery.galleryConfig.galleryLabels.galleryFolderAction.isNotBlank()) {
            actionButton.text = Gallery.galleryConfig.galleryLabels.galleryFolderAction
        }

        actionButton.isAllCaps = Gallery.galleryConfig.textAllCaps
        actionButton.setOnClickListener { onActionButtonClick() }


        baseBinding.customToolbar.apply {
            toolbarTitle.isAllCaps = Gallery.galleryConfig.textAllCaps
            toolbarTitle.gravity = Gallery.galleryConfig.galleryLabels.titleAlignment
        }
    }

    override fun initViewModels() {
        super.initViewModels()
        loadAlbumViewModel.getAlbums().observe(this, Observer { setAlbumData(it) })
        loadAlbumViewModel.loadAlbums()
    }

    override fun onResume() {
        super.onResume()
        galleryActionListener?.showCrossButton()
    }

    override fun onListItemClick(photo: PhotoAlbum) {
        openPhotoGridFragment(photo)
    }

    private fun openPhotoGridFragment(photo: PhotoAlbum) {
        galleryActionListener?.moveToPhotoGrid(photo)
        Gallery.carousalActionListener?.onGalleryFolderSelected()
    }

    override fun onActionButtonClick() {
        super.onActionButtonClick()
        galleryActionListener?.onActionClicked(true)
    }

    override fun setHomeAsUp() = true

    companion object {
        const val COLUMNS_COUNT = 3
        fun getInstance() = FolderViewFragment().apply {
        }
    }

}
