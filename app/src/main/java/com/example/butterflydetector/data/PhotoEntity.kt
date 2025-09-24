package com.example.butterflydetector.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageHash: String, // Hash of the image for identification
    val imagePath: String, // Path to stored image file
    val latitude: Double,
    val longitude: Double,
    val photoTakenTimestamp: Long,
    val photoSentTimestamp: Long,
    val userName: String = "Buttershy"
)
