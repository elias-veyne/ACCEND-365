package com.accend.app.data

import com.accend.app.domain.Curriculum
import com.accend.app.domain.CurriculumTask
import com.accend.app.domain.SkillTrack
import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao: AccendDao) {
    fun observeUser(): Flow<UserEntity?> = dao.observeUser()
    suspend fun saveUser(user: UserEntity) = dao.upsertUser(user)
}

class ProgressRepository(private val dao: AccendDao) {
    fun observeDay(day: Int): Flow<List<DayProgressEntity>> = dao.observeDay(day)
    fun observeAll(): Flow<List<DayProgressEntity>> = dao.observeAllProgress()
    suspend fun complete(userId: String, day: Int, task: CurriculumTask) {
        dao.upsertProgress(DayProgressEntity(userId, day, task.pillarId, task.id, true, System.currentTimeMillis()))
        dao.upsertWeeklyXp(WeeklyXpEntity(userId, ((day - 1) / 7) + 1, task.xp))
    }
}

class CurriculumRepository {
    fun tasksForDay(day: Int, track: String): List<CurriculumTask> = Curriculum.tasksForDay(
        day,
        SkillTrack.entries.firstOrNull { it.label == track } ?: SkillTrack.CODING
    )
}