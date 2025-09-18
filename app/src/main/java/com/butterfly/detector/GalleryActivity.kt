package com.butterfly.detector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.butterfly.detector.adapter.PhotoAdapter
import com.butterfly.detector.databinding.ActivityGalleryBinding
import java.io.File

class GalleryActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityGalleryBinding
    private lateinit var photoAdapter: PhotoAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        loadPhotos()
        setupClickListeners()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter()
        binding.photoRecyclerView.apply {
            layoutManager = GridLayoutManager(this@GalleryActivity, 3)
            adapter = photoAdapter
        }
    }
    
    private fun loadPhotos() {
        val photoDir = getOutputDirectory()
        if (!photoDir.exists()) return
        
        val photoFiles = photoDir.listFiles { file ->
            file.isFile && file.name.lowercase().endsWith(".jpg")
        }?.toList() ?: emptyList()
        
        // For demo purposes, create sample photos with confidence scores
        // In real app, this would come from stored metadata
        val samplePhotos = photoFiles.mapIndexed { index, file ->
            CapturedPhoto(
                filePath = file.absolutePath,
                butterflyClass = getSampleButterflyName(index),
                confidence = 0.95f - (index * 0.05f), // Decreasing confidence
                timestamp = file.lastModified()
            )
        }.sortedByDescending { it.confidence }.take(9) // Top 9 photos
        
        photoAdapter.submitPhotos(samplePhotos)
    }
    
    private fun getSampleButterflyName(index: Int): String {
        val names = listOf(
            "Tagpfauenauge",
            "Admiral", 
            "Schwalbenschwanz",
            "Kleiner Fuchs",
            "Zitronenfalter",
            "Bläuling",
            "Distelfalter",
            "C-Falter",
            "Schachbrettfalter"
        )
        return names.getOrNull(index % names.size) ?: "Unbekannter Schmetterling"
    }
    
    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
    
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "ButterflyPhotos").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}