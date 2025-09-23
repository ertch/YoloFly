package com.example.butterflydetector.ui.home

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Camera Ready - Press camera button to start taking photos"
    }
    val text: LiveData<String> = _text

    private val _isCapturing = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isCapturing: LiveData<Boolean> = _isCapturing

    private val _photoCount = MutableLiveData<Int>().apply {
        value = 0
    }
    val photoCount: LiveData<Int> = _photoCount

    // Store captured photos in memory
    private val _capturedPhotos = mutableListOf<Bitmap>()
    val capturedPhotos: List<Bitmap> get() = _capturedPhotos.toList()

    fun startCapturing() {
        _isCapturing.value = true
        _text.value = "Auto-capturing photos every 0.5s..."
    }

    fun stopCapturing() {
        _isCapturing.value = false
        _text.value = "Photo capture stopped. ${_photoCount.value} photos captured."
    }

    fun addPhoto(bitmap: Bitmap) {
        _capturedPhotos.add(bitmap)
        _photoCount.value = _capturedPhotos.size
        println("[v0] Photo added to ViewModel. Total count: ${_capturedPhotos.size}")
    }

    fun clearPhotos() {
        _capturedPhotos.clear()
        _photoCount.value = 0
        _text.value = "Camera Ready - Press camera button to start taking photos"
    }

    override fun onCleared() {
        super.onCleared()
        // Clear photos from memory when ViewModel is destroyed
        _capturedPhotos.clear()
    }
}
