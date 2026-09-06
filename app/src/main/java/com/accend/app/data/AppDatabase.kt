package com.accend.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UserProfileEntity::class, TaskProgressEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accendDao(): AccendDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val newColumns = listOf(
                    "streakFreezeTokens INTEGER NOT NULL DEFAULT 3",
                    "restDaysUsedThisWeek INTEGER NOT NULL DEFAULT 0",
                    "weeklyReflection TEXT NOT NULL DEFAULT ''",
                    "dailyGoalsPillarIds TEXT NOT NULL DEFAULT ''",
                    "isDarkMode INTEGER NOT NULL DEFAULT 1",
                    "privacyLevel TEXT NOT NULL DEFAULT 'friends_only'",
                    "totalDaysCompleted INTEGER NOT NULL DEFAULT 0",
                    "totalTasksCompleted INTEGER NOT NULL DEFAULT 0",
                    "bestStreak INTEGER NOT NULL DEFAULT 0",
                    "physicalCompleted INTEGER NOT NULL DEFAULT 0",
                    "mentalCompleted INTEGER NOT NULL DEFAULT 0",
                    "skillsCompleted INTEGER NOT NULL DEFAULT 0",
                    "socialCompleted INTEGER NOT NULL DEFAULT 0"
                )
                for (col in newColumns) {
                    db.execSQL("ALTER TABLE user_profile ADD COLUMN $col")
                }
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "accend_database.db"
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
