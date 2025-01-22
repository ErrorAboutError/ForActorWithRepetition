package com.example.foractorwithrepetition

import androidx.room.Entity
import androidx.room.PrimaryKey


// Класс для представления репетиций
@Entity(tableName = "rehearsals")
data class Rehearsal (
    // Id оповещения
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // Название оповещения
    val name: String,
    // Время оповещения
    val time: String,
    // Дата оповещения
    val date: String,
    // Точное время оповещения
    val timeInMiles: Long,
    // Статус оповещения
    var activated: Boolean,
    // Местоположение репитиции
    val location: String,
    val placeName: String
)