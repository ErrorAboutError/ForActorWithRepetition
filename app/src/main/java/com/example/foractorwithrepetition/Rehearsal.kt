package com.example.foractorwithrepetition

import androidx.room.Entity
import androidx.room.PrimaryKey


// Класс для представления репетиций
@Entity(tableName = "rehearsals")
data class Rehearsal (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val time: String,
    val date: String,
    val timeInMiles: Long,
    var activated: Boolean
)