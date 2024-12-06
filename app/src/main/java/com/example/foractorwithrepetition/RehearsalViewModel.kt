package com.example.foractorwithrepetition

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.launch

class RehearsalViewModel(application: Application) : AndroidViewModel(application) {
    private val rehearsalDao: RehearsalDao
    private val database: AppDatabase

    init {
        database = Room.databaseBuilder(application, AppDatabase::class.java, "rehearsal-db").build()
        rehearsalDao = database.rehearsalDao()
    }

    fun getAllRehearsals() = liveData {
        emit(rehearsalDao.getAllRehearsals())
    }

    fun insert(rehearsal: Rehearsal) {
        viewModelScope.launch {
            rehearsalDao.insert(rehearsal)
        }
    }
}