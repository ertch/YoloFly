package com.butterfly.detector

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.butterfly.detector.databinding.ActivityMainBinding
import com.butterfly.detector.ml.ButterflyDetector
import com.butterfly.detector.model.DetectionResult
import com.butterfly.detector.utils.ImageUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var butterflyDetector: ButterflyDetector
    
    private var autoPhotoCount = 0
    private var isManualMode = false
    private val capturedPhotos = mutableListOf<CapturedPhoto>()
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize detector
        butterflyDetector = ButterflyDetector(this)
        
        // Setup executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        
        // Setup UI
        updateModeDisplay()
        setupClickListeners()
        
        // Check permissions and start camera
        checkCameraPermission()
    }
    
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            
            // Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }
            
            // ImageCapture
            imageCapture = ImageCapture.Builder().build()
            
            // ImageAnalysis for real-time detection
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ButterflyAnalyzer())
                }
            
            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
            
        }, ContextCompat.getMainExecutor(this))
    }
    
    private fun setupClickListeners() {
        binding.captureButton.setOnClickListener {
            if (isManualMode) {
                takePhoto()
            }
        }
        
        binding.galleryButton.setOnClickListener {
            startActivity(Intent(this, GalleryActivity::class.java))
        }
    }
    
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        
        val photoFile = File(
            getOutputDirectory(),
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )
        
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
                
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d(TAG, "Photo capture succeeded: ${photoFile.absolutePath}")
                }
            }
        )
    }
    
    private fun autoTakePhoto(detectionResult: DetectionResult) {
        if (autoPhotoCount >= 30) return
        
        val imageCapture = imageCapture ?: return
        
        val photoFile = File(
            getOutputDirectory(),
            "auto_${autoPhotoCount}_${SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())}.jpg"
        )
        
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Auto photo capture failed: ${exception.message}", exception)
                }
                
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    autoPhotoCount++
                    
                    // Save photo info
                    capturedPhotos.add(
                        CapturedPhoto(
                            filePath = photoFile.absolutePath,
                            butterflyClass = detectionResult.className,
                            confidence = detectionResult.confidence,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    
                    runOnUiThread {
                        updateModeDisplay()
                        
                        if (autoPhotoCount >= 30) {
                            switchToManualMode()
                        }
                    }
                    
                    Log.d(TAG, "Auto photo captured: ${photoFile.absolutePath} (${autoPhotoCount}/30)")
                }
            }
        )
    }
    
    private fun switchToManualMode() {
        isManualMode = true
        binding.captureButton.visibility = View.VISIBLE
        updateModeDisplay()
    }
    
    private fun updateModeDisplay() {
        runOnUiThread {
            if (isManualMode) {
                binding.modeText.text = getString(R.string.manual_capture_mode)
            } else {
                binding.modeText.text = getString(R.string.auto_capture_mode, autoPhotoCount)
            }
        }
    }
    
    private fun updateDetectionDisplay(detectionResult: DetectionResult?) {
        runOnUiThread {
            if (detectionResult != null && detectionResult.confidence > 0.5f) {
                binding.detectionText.text = getString(
                    R.string.butterfly_detected,
                    detectionResult.className,
                    detectionResult.confidence * 100
                )
                binding.detectionText.setTextColor(ContextCompat.getColor(this, R.color.green))
            } else {
                binding.detectionText.text = getString(R.string.no_butterfly_detected)
                binding.detectionText.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
        }
    }
    
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "ButterflyPhotos").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }
    
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        butterflyDetector.close()
    }
    
    private inner class ButterflyAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            val bitmap = ImageUtils.imageProxyToBitmap(image)
            val detectionResult = butterflyDetector.detectButterfly(bitmap)
            
            updateDetectionDisplay(detectionResult)
            
            // Auto capture if butterfly detected with high confidence
            if (!isManualMode && detectionResult != null && detectionResult.confidence > 0.7f) {
                autoTakePhoto(detectionResult)
            }
            
            image.close()
        }
    }
    
    companion object {
        private const val TAG = "MainActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}

data class CapturedPhoto(
    val filePath: String,
    val butterflyClass: String,
    val confidence: Float,
    val timestamp: Long
)