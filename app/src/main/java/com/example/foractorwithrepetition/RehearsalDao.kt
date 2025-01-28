package com.example.foractorwithrepetition

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Интерфейс для доступа к БД
@Dao
interface RehearsalDao {
    // Добавление события
    // rehearsal - добавляемое событие
    @Insert
    suspend fun insert(rehearsal: Rehearsal)

    // Получение списка событий
    @Query("SELECT * FROM rehearsals")
    suspend fun getAllRehearsals(): List<Rehearsal>

    // Удаление событий
    // rehearsal - удаляемое событие
    @Delete
    suspend fun delete(rehearsal: Rehearsal)
    // Обновление статуса события
    // id - id события
    // activate - новый статус события
    @Query("UPDATE rehearsals SET activated = :activate WHERE id = :id + 1")
    suspend fun update(id: Long, activate: Boolean)

    // Изменене события
    // rehearsal - изменённое событие
    @Update
    suspend fun updateRehearsal(rehearsal: Rehearsal)
}