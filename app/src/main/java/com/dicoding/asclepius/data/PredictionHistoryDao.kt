package com.dicoding.asclepius.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PredictionHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prediction: PredictionHistory)

    @Query("SELECT * FROM prediction_history ORDER BY id DESC")
    fun getAllPredictions(): LiveData<List<PredictionHistory>>

    @Query("DELETE FROM prediction_history WHERE id = :predictionId")
    suspend fun deletePredictionById(predictionId: Int)
}
