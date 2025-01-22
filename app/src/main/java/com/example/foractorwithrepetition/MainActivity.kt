package com.example.foractorwithrepetition

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE = 2
    private lateinit var rehearsalViewModel: RehearsalViewModel
    private lateinit var rehearsalAdapter: RehearsalAdapter
    private lateinit var rehearsalDate: EditText
    private lateinit var alarmManager: AlarmManager
    private lateinit var timePicker: TimePicker
    private lateinit var toolbarNow: Toolbar
    private lateinit var layoutButton: Button
    private val CHANNEL_ID = "channelid"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
//        layoutButton = findViewById(R.id.newLayoutButton)
//        layoutButton.setOnClickListener{
//            val intent = Intent(this@MainActivity, ActivityWithDrawerNavigation::class.java)
//            startActivity(intent)
//        }
        rehearsalViewModel = ViewModelProvider(this).get(RehearsalViewModel::class.java)
        rehearsalAdapter = RehearsalAdapter(mutableListOf())

        rehearsalDate = findViewById(R.id.rehearsalDate)
        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        rehearsalDate.setOnClickListener { showDatePickerDialog() }
        findViewById<Button>(R.id.addButton).setOnClickListener { onAddButtonClicked() }

        rehearsalViewModel.getAllRehearsals().observe(this) { rehearsals ->
            rehearsalAdapter.updateRehearsals(rehearsals)
        }
    }

    private fun onAddButtonClicked() {
        val name = findViewById<EditText>(R.id.rehearsalName).text.toString()
        val date = rehearsalDate.text.toString()

        if (name.isNotEmpty() && date.isNotEmpty()) {
            checkAndRequestPermissions(name)
        }else{
            Toast.makeText(this, "Вы не заполнили все поля", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkAndRequestPermissions(name: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM, Manifest.permission.USE_EXACT_ALARM), SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE)
        } else {
            if (canScheduleExactAlarms()) {
                setAlarm(name)
            } else {
                Toast.makeText(this, "Точные будильники не могут быть запланированы.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAlarm(name: String) {
        val calendar = Calendar.getInstance().apply {
            val dateParts = rehearsalDate.text.toString().split("/")
            if (dateParts.size == 3) {
                set(Calendar.YEAR, dateParts[2].toInt())
                set(Calendar.MONTH, dateParts[1].toInt() - 1)
                set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())
                set(Calendar.HOUR_OF_DAY, timePicker.hour)
                set(Calendar.MINUTE, timePicker.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }

        val alarmIntent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("rehearsal_name", name)
        }.let {
            PendingIntent.getBroadcast(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
        //rehearsalViewModel.insert(Rehearsal(name = name, time = "${timePicker.hour}:${timePicker.minute}", date = "42", activated = true))
        findViewById<EditText>(R.id.rehearsalName).text.clear()
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val name = findViewById<EditText>(R.id.rehearsalName).text.toString()
                if (name.isNotEmpty() && canScheduleExactAlarms()) {
                    setAlarm(name)
                } else {
                    Toast.makeText(this, "Точные будильники не могут быть запланированы.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Разрешение для установки будильника отклонено.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Rehearsal Notifications", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Channel for rehearsal notifications"
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this,
            { _: DatePicker?, year: Int, month: Int, day: Int ->
                rehearsalDate.setText("$day/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
