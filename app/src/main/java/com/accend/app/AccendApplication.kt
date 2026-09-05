package com.accend.app

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.accend.app.data.AccendDatabase

class AccendApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(this, AccendDatabase::class.java, "accend.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS achievements (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL, icon TEXT NOT NULL, unlocked INTEGER NOT NULL, unlockedAt INTEGER)")
                database.execSQL("CREATE TABLE IF NOT EXISTS weekly_xp (userId TEXT NOT NULL, weekNumber INTEGER NOT NULL, xp INTEGER NOT NULL, PRIMARY KEY(userId, weekNumber))")
            }
        }
    }
}