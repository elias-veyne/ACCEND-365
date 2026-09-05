package com.accend.app.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AccendDao {
    @Query("SELECT * FROM users WHERE id = 'local-user'")
    fun observeUser(): Flow<UserEntity?>

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Query("SELECT * FROM dayprogressentity WHERE userId = 'local-user' AND dayNumber = :day")
    fun observeDay(day: Int): Flow<List<DayProgressEntity>>

    @Upsert
    suspend fun upsertProgress(progress: DayProgressEntity)

    @Query("SELECT * FROM dayprogressentity WHERE userId = 'local-user'")
    fun observeAllProgress(): Flow<List<DayProgressEntity>>

    @Upsert
    suspend fun upsertWeeklyXp(weeklyXp: WeeklyXpEntity)

    @Query("SELECT * FROM quotes ORDER BY id")
    fun observeQuotes(): Flow<List<QuoteEntity>>

    @Upsert
    suspend fun upsertQuotes(quotes: List<QuoteEntity>)

    @Query("SELECT * FROM achievements ORDER BY unlocked DESC, id")
    fun observeAchievements(): Flow<List<AchievementEntity>>

    @Upsert
    suspend fun upsertAchievements(achievements: List<AchievementEntity>)
}