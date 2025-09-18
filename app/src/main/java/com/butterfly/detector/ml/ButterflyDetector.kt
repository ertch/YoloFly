package com.butterfly.detector.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.butterfly.detector.model.DetectionResult
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ButterflyDetector(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()
    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(INPUT_HEIGHT, INPUT_WIDTH, ResizeOp.ResizeMethod.BILINEAR))
        .build()
    
    init {
        loadModel()
        loadLabels()
    }
    
    private fun loadModel() {
        try {
            val modelBuffer = FileUtil.loadMappedFile(context, MODEL_PATH)
            val options = Interpreter.Options()
            
            // Use GPU delegate if available
            try {
                // val gpuDelegate = GpuDelegate()
                // options.addDelegate(gpuDelegate)
            } catch (e: Exception) {
                Log.w(TAG, "GPU delegate not available, using CPU", e)
            }
            
            interpreter = Interpreter(modelBuffer, options)
            Log.d(TAG, "Model loaded successfully")
        } catch (e: IOException) {
            Log.e(TAG, "Error loading model", e)
        }
    }
    
    private fun loadLabels() {
        try {
            labels = FileUtil.loadLabels(context, LABELS_PATH)
            Log.d(TAG, "Labels loaded: ${labels.size} classes")
        } catch (e: IOException) {
            Log.e(TAG, "Error loading labels", e)
            // Fallback labels for testing
            labels = listOf(
                "Admiral",
                "Bläuling", 
                "Schwalbenschwanz",
                "Weißling",
                "Schachbrettfalter",
                "Tagpfauenauge",
                "Kleiner Fuchs",
                "C-Falter",
                "Distelfalter",
                "Zitronenfalter"
            )
        }
    }
    
    fun detectButterfly(bitmap: Bitmap): DetectionResult? {
        val interpreter = this.interpreter ?: return null
        
        try {
            // Preprocess image
            val tensorImage = TensorImage.fromBitmap(bitmap)
            val processedImage = imageProcessor.process(tensorImage)
            
            // Prepare input tensor
            val inputBuffer = processedImage.buffer
            
            // Prepare output tensor
            val outputBuffer = ByteBuffer.allocateDirect(4 * labels.size)
            outputBuffer.order(ByteOrder.nativeOrder())
            
            // Run inference
            interpreter.run(inputBuffer, outputBuffer)
            
            // Process output
            outputBuffer.rewind()
            val probabilities = FloatArray(labels.size)
            outputBuffer.asFloatBuffer().get(probabilities)
            
            // Find best prediction
            val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
            val confidence = probabilities[maxIndex]
            
            // Only return result if confidence is above threshold
            if (confidence > CONFIDENCE_THRESHOLD) {
                return DetectionResult(
                    className = labels[maxIndex],
                    confidence = confidence,
                    classIndex = maxIndex
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during inference", e)
        }
        
        return null
    }
    
    fun close() {
        interpreter?.close()
        interpreter = null
    }
    
    companion object {
        private const val TAG = "ButterflyDetector"
        private const val MODEL_PATH = "butterfly_model.tflite"
        private const val LABELS_PATH = "butterfly_labels.txt"
        private const val INPUT_WIDTH = 224
        private const val INPUT_HEIGHT = 224
        private const val CONFIDENCE_THRESHOLD = 0.3f
    }
}