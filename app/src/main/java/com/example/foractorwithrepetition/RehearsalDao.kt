package com.example.foractorwithrepetition

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

// Интерфейс для доступа к БД
@Dao
interface RehearsalDao {
    // Добавление оповещения
    // rehearsal - добавляемое оповещение
    @Insert
    suspend fun insert(rehearsal: Rehearsal)

    // Получение списка оповещений
    @Query("SELECT * FROM rehearsals")
    suspend fun getAllRehearsals(): List<Rehearsal>

    // Удаление оповещения
    // rehearsal - удаляемое оповещение
    @Delete
    suspend fun delete(rehearsal: Rehearsal)
    // Обновление статуса оповещения
    // id - id оповещения
    // activate - новый статус оповещения
    @Query("UPDATE rehearsals SET activated = :activate WHERE id = :id + 1")
    suspend fun update(id: Long, activate: Boolean)
}