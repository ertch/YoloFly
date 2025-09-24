package com.example.butterflydetector.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.butterflydetector.databinding.FragmentHomeBinding
import com.example.butterflydetector.ml.ButterflyDetector
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.io.path.createTempFile

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private lateinit var butterflyDetector: ButterflyDetector
    private var isDetectionEnabled = false
    private var currentButterflyCount = 0

    private val captureHandler = Handler(Looper.getMainLooper())
    private var captureRunnable: Runnable? = null
    private var isAutoCapturing = false

    companion object {
        private const val TAG = "HomeFragment"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val CAPTURE_INTERVAL_MS = 500L // 0.5 seconds
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        butterflyDetector = ButterflyDetector.getInstance(requireContext())

        // Initialize detector
        lifecycleScope.launch {
            val initialized = butterflyDetector.initialize()
            if (initialized) {
                homeViewModel.updateDetectionStatus("Detection: Ready")

                // Start analyzing frames AFTER detector is ready
                imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
                    processImageForButterflyDetection(imageProxy)
                }
            } else {
                homeViewModel.updateDetectionStatus("Detection: Failed to load model")
                Toast.makeText(
                    requireContext(),
                    "Failed to load butterfly model. Make sure butterfly_model.onnx is in assets folder",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Observe ViewModel
        homeViewModel.text.observe(viewLifecycleOwner) { binding.statusText.text = it }
        homeViewModel.photoCount.observe(viewLifecycleOwner) { count ->
            binding.photoCountText.text = "Photos captured: $count"
        }
        homeViewModel.butterflyCount.observe(viewLifecycleOwner) { count ->
            binding.butterflyCountText.text = "Butterflies detected: $count"
        }
        homeViewModel.detectionStatus.observe(viewLifecycleOwner) { status ->
            binding.detectionStatusText.text = status
        }
        homeViewModel.isCapturing.observe(viewLifecycleOwner) { isCapturing ->
            if (isCapturing && !isAutoCapturing) startAutoCapture()
            else if (!isCapturing && isAutoCapturing) stopAutoCapture()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Request camera permissions and start camera
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted() && cameraProvider == null) startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()

            imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
                if (isDetectionEnabled) processImageForButterflyDetection(imageProxy)
                else imageProxy.close()
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                camera = cameraProvider?.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                Toast.makeText(requireContext(), "Camera initialization failed", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processImageForButterflyDetection(imageProxy: ImageProxy) {
        try {
            val bitmap = imageProxyToBitmap(imageProxy)

            lifecycleScope.launch {
                try {
                    val isDetected = butterflyDetector.detectButterfly(bitmap)

                    // Log for debugging
                    Log.d(TAG, "Max confidence and detection handled in ButterflyDetector")
                    Log.d(TAG, "Butterfly detected: $isDetected")

                    currentButterflyCount = if (isDetected) 1 else 0
                    homeViewModel.updateButterflyCount(currentButterflyCount)

                    val status = if (isDetected) "Detection: Butterfly found!" else "Detection: No butterfly"
                    homeViewModel.updateDetectionStatus(status)

                } catch (e: Exception) {
                    Log.e(TAG, "Error in butterfly detection", e)
                    homeViewModel.updateDetectionStatus("Detection: Error")
                } finally {
                    imageProxy.close()
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error converting image for detection", e)
            imageProxy.close()
        }
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val buffer = imageProxy.planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return Bitmap.createBitmap(
            imageProxy.width,
            imageProxy.height,
            Bitmap.Config.ARGB_8888
        ).also { bitmap ->
            bitmap.copyPixelsFromBuffer(java.nio.ByteBuffer.wrap(bytes))
        }
    }

    private fun startAutoCapture() {
        if (isAutoCapturing) return
        isAutoCapturing = true
        captureRunnable = object : Runnable {
            override fun run() {
                if (isAutoCapturing && imageCapture != null) {
                    capturePhoto()
                    captureHandler.postDelayed(this, CAPTURE_INTERVAL_MS)
                }
            }
        }
        captureHandler.post(captureRunnable!!)
    }

    private fun stopAutoCapture() {
        isAutoCapturing = false
        captureRunnable?.let { captureHandler.removeCallbacks(it) }
        captureRunnable = null
    }

    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return
        val tempFile = File.createTempFile("photo", ".jpg", requireContext().cacheDir)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    try {
                        val file = output.savedUri?.path?.let { java.io.File(it) }
                        if (file?.exists() == true) {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            if (bitmap != null) homeViewModel.addPhoto(bitmap)
                            file.delete()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing captured photo", e)
                    }
                }
            }
        )
    }

    fun captureAdditionalPhoto() {
        if (imageCapture != null) {
            if (homeViewModel.isCapturing.value == true) capturePhoto()
            else homeViewModel.startCapturing()
        }
    }

    fun stopPhotoCapture() {
        homeViewModel.stopCapturing()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) startCamera()
            else {
                Toast.makeText(requireContext(), "Permissions not granted", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoCapture()
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
        butterflyDetector.cleanup()
        _binding = null
    }
}
