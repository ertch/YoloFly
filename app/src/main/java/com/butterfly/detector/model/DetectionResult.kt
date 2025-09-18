package com.butterfly.detector.model

data class DetectionResult(
    val className: String,
    val confidence: Float,
    val classIndex: Int
)