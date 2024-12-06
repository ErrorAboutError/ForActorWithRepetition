package com.example.foractorwithrepetition

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 800 // Время в миллисекундах
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        // Создаем задержку перед переходом к MainActivity
        Handler().postDelayed({
            val intent = Intent(this,  ActivityWithDrawerNavigation::class.java)
            startActivity(intent)
            finish() // Закрываем SplashActivity
        }, SPLASH_TIME_OUT)
    }

}