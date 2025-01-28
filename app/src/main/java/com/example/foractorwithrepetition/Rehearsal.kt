package com.example.foractorwithrepetition

import androidx.room.Entity
import androidx.room.PrimaryKey


// Класс для представления событий
@Entity(tableName = "rehearsals")
data class Rehearsal (
    // Id оповещения
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // Название события
    val name: String,
    // Время события
    val time: String,
    // Дата события
    val date: String,
    // Точное время события
    val timeInMiles: Long,
    // Статус события
    var activated: Boolean,
    // Местоположение события
    val location: String,
    // Адрес события
    val placeName: String
)