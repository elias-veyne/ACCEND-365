package com.accend.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccendDao {

    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM task_progress WHERE dayNumber = :dayNumber")
    fun getProgressForDay(dayNumber: Int): Flow<List<TaskProgressEntity>>

    @Query("SELECT * FROM task_progress WHERE isCompleted = 1")
    fun getAllCompletedProgressFlow(): Flow<List<TaskProgressEntity>>

    @Query("SELECT * FROM task_progress WHERE isCompleted = 1")
    suspend fun getAllCompletedProgressSync(): List<TaskProgressEntity>

    @Query("SELECT * FROM task_progress WHERE id = :id LIMIT 1")
    suspend fun getTaskProgressById(id: String): TaskProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: TaskProgressEntity)

    @Query("DELETE FROM task_progress WHERE id = :id")
    suspend fun deleteProgressById(id: String)
}
