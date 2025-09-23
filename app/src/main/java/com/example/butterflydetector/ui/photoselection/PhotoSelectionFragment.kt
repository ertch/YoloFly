package com.example.butterflydetector.ui.photoselection

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.butterflydetector.databinding.FragmentPhotoselectionBinding
import com.example.butterflydetector.ui.home.HomeViewModel

class PhotoSelectionFragment : Fragment() {

    private var _binding: FragmentPhotoselectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoselectionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        setupRecyclerView()
        observePhotos()

        return root
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(emptyList())
        binding.photosRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3) // 3 columns
            adapter = photoAdapter
        }
    }

    private fun observePhotos() {
        homeViewModel.photoCount.observe(viewLifecycleOwner) { count ->
            Log.d("PhotoSelectionFragment", "[v0] PhotoSelection observing photo count: $count")
            val photos = homeViewModel.capturedPhotos
            Log.d("PhotoSelectionFragment", "[v0] Actual photos list size: ${photos.size}")

            photoAdapter.updatePhotos(photos)

            binding.textGallery.text = if (count > 0) {
                "Recently captured photos ($count)"
            } else {
                "No photos captured yet. Press the camera button to start taking photos."
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PhotoAdapter(private var photos: List<Bitmap>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(android.R.id.icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val imageView = ImageView(parent.context).apply {
            id = android.R.id.icon
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                400 // Increased height for better visibility
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(4, 4, 4, 4) // Add some padding between images
        }
        return PhotoViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        Log.d("PhotoAdapter", "[v0] Binding photo at position $position")
        holder.imageView.setImageBitmap(photos[position])
    }

    override fun getItemCount() = photos.size

    fun updatePhotos(newPhotos: List<Bitmap>) {
        Log.d("PhotoAdapter", "[v0] PhotoAdapter updating with ${newPhotos.size} photos")
        photos = newPhotos
        notifyDataSetChanged()
    }
}
