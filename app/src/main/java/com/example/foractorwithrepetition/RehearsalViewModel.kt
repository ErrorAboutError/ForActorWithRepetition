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
        database = Room.databaseBuilder(application, AppDatabase::class.java, "rehearsalv1.12-db").build()
        rehearsalDao = database.rehearsalDao()
    }

    fun getAllRehearsals() = liveData {
        emit(rehearsalDao.getAllRehearsals())
    }

    fun updateActivation(position: Long, activated: Boolean){
        viewModelScope.launch {
            rehearsalDao.update(position, activated)
        }
    }
    fun insert(rehearsal: Rehearsal) {
        viewModelScope.launch {
            rehearsalDao.insert(rehearsal)
        }
    }
}