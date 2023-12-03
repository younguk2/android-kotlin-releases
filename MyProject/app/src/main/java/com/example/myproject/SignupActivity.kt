package com.example.myproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity: AppCompatActivity() {
    private val mainButton by lazy { findViewById<Button>(R.id.goMain_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        findViewById<Button>(R.id.signup)?.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.emailAddress)?.text.toString()
            val password = findViewById<EditText>(R.id.password)?.text.toString()
            createUserWithEmailAndPassword(userEmail, password)
        }

        mainButton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun createUserWithEmailAndPassword(userEmail: String,password: String){
        Firebase.auth.createUserWithEmailAndPassword(userEmail,password)
            .addOnCompleteListener(this){
                if (it.isSuccessful) {
                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}