package com.example.foractorwithrepetition

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.material.button.MaterialButton

class LoginRegistration : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val REQUEST_CODE_PERMISSION = 1001
    private val REQUEST_CODE_SIGN_IN = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_registration)
        val lastInGoogle = GoogleSignIn.getLastSignedInAccount(this)
        if(lastInGoogle!=null){
            val intent = Intent(this, ActivityWithDrawerNavigation::class.java)
            startActivity(intent)
            finish()
        }
        // Настройка авторизации Google
        setupGoogleSignIn()

        // Обработка нажатия кнопки Google Sign-In
        val googleSignInButton = findViewById<ImageButton>(R.id.signin_google)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                Toast.makeText(this, "Авторизация успешна!", Toast.LENGTH_SHORT).show()
                // Переход на новую активность после успешного входа
                val intent = Intent(this, ActivityWithDrawerNavigation::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Ошибка входа в Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupGoogleSignIn() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/calendar"))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, options)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
//        val intent = Intent(this,  ActivityWithDrawerNavigation::class.java)
//        startActivity(intent)
//        finish()
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.GET_ACCOUNTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.GET_ACCOUNTS),
                REQUEST_CODE_PERMISSION
            )
        }
    }
}
