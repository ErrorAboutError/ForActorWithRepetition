package com.example.foractorwithrepetition

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Интерфейс для доступа к БД
@Dao
interface RehearsalDao {
    @Insert
    suspend fun insert(rehearsal: Rehearsal)

    @Query("SELECT * FROM rehearsals")
    suspend fun getAllRehearsals(): List<Rehearsal>

    @Delete
    suspend fun delete(rehearsal: Rehearsal)

    @Query("UPDATE rehearsals SET activated = :activate WHERE id = :id + 1")
    suspend fun update(id: Long, activate: Boolean)
}