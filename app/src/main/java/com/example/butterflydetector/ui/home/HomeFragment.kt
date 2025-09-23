package com.example.butterflydetector.ui.home

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.butterflydetector.databinding.FragmentHomeBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

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

        // Observe ViewModel
        homeViewModel.text.observe(viewLifecycleOwner) {
            binding.statusText.text = it
        }

        homeViewModel.photoCount.observe(viewLifecycleOwner) { count ->
            binding.photoCountText.text = "Photos captured: $count"
        }

        homeViewModel.isCapturing.observe(viewLifecycleOwner) { isCapturing ->
            if (isCapturing && !isAutoCapturing) {
                startAutoCapture()
            } else if (!isCapturing && isAutoCapturing) {
                stopAutoCapture()
            }
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
        // Start camera when fragment becomes visible
        if (allPermissionsGranted() && cameraProvider == null) {
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // ImageAnalysis for continuous capture
            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider?.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider?.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

                // Camera is ready but not capturing automatically

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                Toast.makeText(requireContext(), "Camera initialization failed", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
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

        // Create in-memory output options
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            createTempFile("photo", ".jpg", requireContext().cacheDir)
        ).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Load the captured image as bitmap and store in memory
                    try {
                        val file = output.savedUri?.path?.let { java.io.File(it) }
                        if (file?.exists() == true) {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            if (bitmap != null) {
                                homeViewModel.addPhoto(bitmap)
                                Log.d(TAG, "Photo captured and stored in memory. Total: ${homeViewModel.photoCount.value}")
                            } else {
                                Log.e(TAG, "Failed to decode bitmap from file")
                            }
                            // Delete the temporary file
                            file.delete()
                        } else {
                            Log.e(TAG, "Temporary file not found")
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
            if (homeViewModel.isCapturing.value == true) {
                // If already capturing, take an additional photo
                capturePhoto()
                Toast.makeText(requireContext(), "Additional photo captured!", Toast.LENGTH_SHORT).show()
            } else {
                // If not capturing, start the capture process
                homeViewModel.startCapturing()
                Toast.makeText(requireContext(), "Photo capture started!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun stopPhotoCapture() {
        homeViewModel.stopCapturing()
        Toast.makeText(requireContext(), "Photo capture stopped", Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoCapture()
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
        _binding = null
    }
}
