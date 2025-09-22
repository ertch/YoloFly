package com.example.butterflydetector.data.camera

import android.graphics.Bitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraRepository @Inject constructor() {

    private val _capturedPhotos = MutableStateFlow<List<CapturedPhoto>>(emptyList())
    val capturedPhotos: StateFlow<List<CapturedPhoto>> = _capturedPhotos.asStateFlow()

    // Simple in-memory cache (id -> CapturedPhoto)
    private val photoCache = ConcurrentHashMap<String, CapturedPhoto>()

    fun addPhoto(bitmap: Bitmap): CapturedPhoto {
        val id = System.currentTimeMillis().toString()
        val photo = CapturedPhoto(id, bitmap, System.currentTimeMillis())
        _capturedPhotos.value = _capturedPhotos.value + photo
        savePhotoToCache(photo)
        return photo
    }

    private fun savePhotoToCache(photo: CapturedPhoto) {
        photoCache[photo.id] = photo
    }

    fun getPhotoFromCache(id: String): CapturedPhoto? {
        return photoCache[id]
    }

    fun clearCache() {
        photoCache.clear()
    }

    fun getCacheSize(): Int {
        return photoCache.size
    }
}
