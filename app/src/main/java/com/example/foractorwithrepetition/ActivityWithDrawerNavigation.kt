package com.example.foractorwithrepetition

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foractorwithrepetition.databinding.ActivityWithDrawerNavigationBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.search.SearchFactory

class ActivityWithDrawerNavigation : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityWithDrawerNavigationBinding
    private val SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE = 1
    private val AUDIO_PERMISSION_REQUEST_CODE = 200
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    private fun setApiKey(savedInstanceState: Bundle?) {
        val haveApiKey = savedInstanceState?.getBoolean("haveApiKey")
            ?: false // При первом запуске приложения всегда false
        if (!haveApiKey) {
            MapKitFactory.setApiKey("33bad7a0-835a-4649-b87e-0c8ac0567432")
        }
    } // API-ключ должен быть задан единожды перед инициализацией MapKitFactory


    // Проверки для разрешения уведомлений
    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM), SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Точные будильники не могут быть запланированы.", Toast.LENGTH_SHORT).show()
        }
    }
    // Проверки разрешения голосового взаимодействия
    private fun checkAndRequestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), AUDIO_PERMISSION_REQUEST_CODE)
        } else {
            // Разрешение предоставлено, можно выполнять действия с записью
            Toast.makeText(this, "Разрешение на запись аудио уже предоставлено.", Toast.LENGTH_SHORT).show()
        }
    }


override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
        SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (canScheduleExactAlarms()) {
                    // Ваш код для установки будильника, если требуется
                }
            } else {
                Toast.makeText(this, "Разрешение для установки будильника отклонено.", Toast.LENGTH_SHORT).show()
            }
        }
        AUDIO_PERMISSION_REQUEST_CODE -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение на запись аудио предоставлено.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Разрешение на запись аудио отклонено.", Toast.LENGTH_SHORT).show()
            }
        }
    }
//    if (requestCode == SCHEDULE_EXACT_ALARM_PERMISSION_REQUEST_CODE) {
//        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            //val name = binding.rehearsalName.text.toString()
//            if (canScheduleExactAlarms()) {
//                //setAlarm(name)
//            } else {
//                Toast.makeText(this@ActivityWithDrawerNavigation, "Точные будильники не могут быть запланированы.", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            Toast.makeText(this, "Разрешение для установки будильника отклонено.", Toast.LENGTH_SHORT).show()
//        }
//    }else{
//        Toast.makeText(this, "ГАСИТСЯ ПОЧЕМУ.", Toast.LENGTH_SHORT).show()
//    }
}
private fun canScheduleExactAlarms(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else true
}

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setApiKey(savedInstanceState)
        MapKitFactory.initialize(this)
            // SearchFactory.initialize(this)
        binding = ActivityWithDrawerNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkAndRequestPermissions()
        checkAndRequestAudioPermission()  // Вызов функции для проверки разрешения на запись аудио

        setSupportActionBar(binding.appBarActivityWithDrawerNavigation.toolbar)

        binding.appBarActivityWithDrawerNavigation.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController =
            findNavController(R.id.nav_host_fragment_content_activity_with_drawer_navigation)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_qr, R.id.nav_qr_generate,  R.id.nav_detail_theatre
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_with_drawer_navigation, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.nav_host_fragment_content_activity_with_drawer_navigation)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}