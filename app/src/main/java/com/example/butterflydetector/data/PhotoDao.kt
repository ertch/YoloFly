package com.example.butterflydetector.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoDao {
    @Insert
    suspend fun insertPhoto(photo: PhotoEntity): Long

    @Insert
    suspend fun insertPhotos(photos: List<PhotoEntity>): List<Long>

    @Query("SELECT * FROM photos ORDER BY photoTakenTimestamp DESC")
    suspend fun getAllPhotos(): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhotoById(id: Long): PhotoEntity?

    @Query("DELETE FROM photos")
    suspend fun deleteAllPhotos()
}
