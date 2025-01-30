package com.example.foractorwithrepetition

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.foractorwithrepetition.ui.home.HomeFragment
import com.example.foractorwithrepetition.ui.scanQR.ScanQRFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class RehearsalViewModel(application: Application) : AndroidViewModel(application) {
    private val rehearsalDao: RehearsalDao
    private val database: AppDatabase
    val db = Firebase.firestore

    companion object{
        var scanning = false
    }

    // Получение доступа к БД/создание БД
    init {
        database = Room.databaseBuilder(application, AppDatabase::class.java, "rehearsalv2.02-db").build()
        rehearsalDao = database.rehearsalDao()
    }

    // Получение списка событий
    fun getAllRehearsals():LiveData<List<Rehearsal>> = liveData {
        val localData = rehearsalDao.getAllRehearsals().toMutableList()
        if(localData.size != 0)
        for(i in 0..localData.size - 1){
            Log.i("Data", localData[i].shareID.toString())
            // Загрузка данных из FireBase
           db.collection("rehearsals").document(localData[i].shareID!!).get()
                .addOnSuccessListener { documentReference ->
                    // Обновление локальных данных
                    val data = documentReference.get("Rehearsal") as HashMap<String, String>
                    val newRehearsal = Rehearsal(
                        id = data.get("id")!! as Long,
                        name = data.get("name")!!,
                        date = data.get("date")!!,
                        activated = true,
                        location = data.get("location")!!,
                        placeName = data.get("placeName")!!,
                        time = data.get("time")!!,
                        timeInMiles = data.get("timeInMiles")!! as Long,
                        shareID = localData[i].shareID
                    )
                    localData[i] = newRehearsal
                    updateRehearsal(newRehearsal)
                }
        }
        emit(localData)
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
    fun insert(rehearsal: Rehearsal, changing: Boolean) {
        if (!changing) {
            // Сохранение в FireBase, если до этого не создавалось
            val rehearsalInfo = hashMapOf(
                "Rehearsal" to rehearsal,
            )
            rehearsalInfo.remove("shareID")
            db.collection("rehearsals")
                .add(rehearsalInfo)
                .addOnSuccessListener { documentReference ->
                    Log.d("MyLog", "Запись добавлена с ID: ${documentReference.id}")
                    viewModelScope.launch {
                        rehearsal.shareID = documentReference.id
                        rehearsalDao.insert(rehearsal)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("MyLog", "Error adding document", e)
                }
        } else {
            // Сохранение в локальную БД
            viewModelScope.launch {
                Log.i("ErrorId", rehearsal.toString())
                rehearsalDao.insert(rehearsal)
            }
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
        val rehearsalInfo = hashMapOf(
            "Rehearsal" to rehearsal,
        )
        rehearsalInfo.remove("shareID")
        db.collection("rehearsals").document(rehearsal.shareID!!).update(rehearsalInfo as Map<String, Any>)
        viewModelScope.launch {
            rehearsalDao.updateRehearsal(rehearsal)
        }
    }

    fun checkShareID(shareID: String){
        // Загрузка данных
        db.collection("rehearsals").document(shareID).get()
            .addOnSuccessListener { documentReference ->
                Log.i("Result", documentReference.get("Rehearsal").toString())
                // Открытие изменения события
                val data = documentReference.get("Rehearsal") as HashMap<String, String>
                val newRehearsal = Rehearsal(
                    id = data.get("id")!! as Long,
                    name = data.get("name")!!,
                    date = data.get("date")!!,
                    activated = true,
                    location = data.get("location")!!,
                    placeName = data.get("placeName")!!,
                    time = data.get("time")!!,
                    timeInMiles = data.get("timeInMiles")!! as Long,
                    shareID = shareID
                )
                HomeFragment.changingRehearsal = newRehearsal
                HomeFragment.loadingRehearsal = true
                ScanQRFragment.navControler.navigate(R.id.nav_home)
                scanning = false
            }
            .addOnFailureListener {
                scanning = false
            }

    }
}