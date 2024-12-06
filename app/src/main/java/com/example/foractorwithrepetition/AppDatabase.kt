package com.example.foractorwithrepetition

import androidx.room.Database
import androidx.room.RoomDatabase
// Класс БД
@Database(entities = [Rehearsal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rehearsalDao(): RehearsalDao
}
