package com.example.butterflydetector.domain.camera

import android.graphics.Bitmap
import com.example.butterflydetector.data.camera.CameraRepository
import com.example.butterflydetector.data.camera.CapturedPhoto
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Use case layer to interact with the camera repository.
 * Keeps your UI code clean and separates business logic.
 */
class CameraUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {
    val capturedPhotos: StateFlow<List<CapturedPhoto>> = cameraRepository.capturedPhotos

    fun capturePhoto(bitmap: Bitmap): CapturedPhoto {
        return cameraRepository.addPhoto(bitmap)
    }

    fun getPhotoById(id: String): CapturedPhoto? {
        return cameraRepository.getPhotoFromCache(id)
    }

    fun clearCache() {
        cameraRepository.clearCache()
    }

    fun getCacheSize(): Int {
        return cameraRepository.getCacheSize()
    }
}
