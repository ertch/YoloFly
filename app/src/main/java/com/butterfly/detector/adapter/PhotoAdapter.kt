package com.butterfly.detector.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.butterfly.detector.CapturedPhoto
import com.butterfly.detector.R
import com.butterfly.detector.databinding.ItemPhotoBinding
import java.text.SimpleDateFormat
import java.util.*

class PhotoAdapter : ListAdapter<CapturedPhoto, PhotoAdapter.PhotoViewHolder>(PhotoDiffCallback()) {
    
    private val photos = mutableListOf<CapturedPhoto>()
    
    fun submitPhotos(newPhotos: List<CapturedPhoto>) {
        photos.clear()
        photos.addAll(newPhotos)
        submitList(photos.toList())
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class PhotoViewHolder(private val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        
        private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        
        fun bind(photo: CapturedPhoto) {
            // Load image with Glide
            Glide.with(binding.root.context)
                .load(photo.filePath)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(binding.photoImageView)
            
            // Set text data
            binding.butterflyNameText.text = photo.butterflyClass
            binding.confidenceText.text = binding.root.context.getString(
                R.string.confidence_threshold,
                photo.confidence * 100
            )
            binding.timestampText.text = timeFormat.format(Date(photo.timestamp))
            
            // Set confidence color
            val confidenceColor = when {
                photo.confidence >= 0.8f -> R.color.green
                photo.confidence >= 0.6f -> R.color.orange
                else -> R.color.red
            }
            binding.confidenceText.setTextColor(
                binding.root.context.getColor(confidenceColor)
            )
        }
    }
    
    private class PhotoDiffCallback : DiffUtil.ItemCallback<CapturedPhoto>() {
        override fun areItemsTheSame(oldItem: CapturedPhoto, newItem: CapturedPhoto): Boolean {
            return oldItem.filePath == newItem.filePath
        }
        
        override fun areContentsTheSame(oldItem: CapturedPhoto, newItem: CapturedPhoto): Boolean {
            return oldItem == newItem
        }
    }
}