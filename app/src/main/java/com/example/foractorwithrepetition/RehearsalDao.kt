package com.example.foractorwithrepetition

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
// Интерфейс для доступа к БД
@Dao
interface RehearsalDao {
    @Insert
    suspend fun insert(rehearsal: Rehearsal)

    @Query("SELECT * FROM rehearsals")
    suspend fun getAllRehearsals(): List<Rehearsal>

    @Delete
    suspend fun delete(rehearsal: Rehearsal)
}