package com.accend.app.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, DayProgressEntity::class, QuoteEntity::class, AchievementEntity::class, WeeklyXpEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AccendDatabase : RoomDatabase() {
    abstract fun accendDao(): AccendDao
}