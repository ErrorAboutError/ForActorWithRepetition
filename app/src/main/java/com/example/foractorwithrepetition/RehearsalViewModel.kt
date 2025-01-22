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

    // Получение доступа к БД/создание БД
    init {
        database = Room.databaseBuilder(application, AppDatabase::class.java, "rehearsalv1.14-db").build()
        rehearsalDao = database.rehearsalDao()
    }

    // Получение списка оповещений
    fun getAllRehearsals() = liveData {
        emit(rehearsalDao.getAllRehearsals())
    }

    // Обновление статуса оповещения
    // id - id оповещения
    // activated - новый статус оповещения
    fun updateActivation(id: Long, activated: Boolean){
        viewModelScope.launch {
            rehearsalDao.update(id, activated)
        }
    }
    // Добавление оповещения
    // rehearsal - новое оповещение
    fun insert(rehearsal: Rehearsal) {
        viewModelScope.launch {
            rehearsalDao.insert(rehearsal)
        }
    }
}