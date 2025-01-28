package com.example.foractorwithrepetition

import android.app.Application
import android.util.Log
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
        database = Room.databaseBuilder(application, AppDatabase::class.java, "rehearsalv1.20-db").build()
        rehearsalDao = database.rehearsalDao()
    }

    // Получение списка событий
    fun getAllRehearsals() = liveData {
        emit(rehearsalDao.getAllRehearsals())
    }

    // Обновление статуса события
    // id - id события
    // activated - новый статус события
    fun updateActivation(id: Long, activated: Boolean){
        viewModelScope.launch {
            rehearsalDao.update(id, activated)
        }
    }

    // Добавление события
    // rehearsal - новое событие
    fun insert(rehearsal: Rehearsal) {
        viewModelScope.launch {
            rehearsalDao.insert(rehearsal)
        }
    }

    // Удаление оповещения
    // rehearsal - удаляемое оповещение
    fun delete(rehearsal: Rehearsal){
        viewModelScope.launch {
            rehearsalDao.delete(rehearsal)
        }
    }

    // Изменение оповещения
    // rehearsal - изменённое оповещение
    fun updateRehearsal(rehearsal: Rehearsal){
        Log.i("new", rehearsal.toString())
        viewModelScope.launch {
            rehearsalDao.updateRehearsal(rehearsal)
            Log.i("new", rehearsal.toString())
        }
    }
}