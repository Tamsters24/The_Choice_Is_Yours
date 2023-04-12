package com.example.thechoiceisyours

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserLogin : AppCompatActivity() {
    private lateinit var bookLogin: FirebaseAuth
    private var userEmail: String = ""
    private var userPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        bookLogin = Firebase.auth
        FirebaseApp.initializeApp(this)

        // Button
        // Navigate to Main Activity Screen: activity_main.xml & MainActivity.kt
        findViewById<ImageButton>(R.id.loginToMainBtn).setOnClickListener {
            val loginToMainIntent = Intent(this, MainActivity::class.java)
            startActivity(loginToMainIntent)
        }

        // Button
        // Navigate to Create Account Screen: activity_user_registration.xml & UserRegistration.kt
        findViewById<Button>(R.id.loginToRegistrationBtn).setOnClickListener {
            val loginToCreateAcctIntent = Intent(this, UserRegistration::class.java)
            startActivity(loginToCreateAcctIntent)
        }

        // Button
        // Navigate to Forgot Password Screen: activity_forgot_password.xml & ForgotPassword.kt
        findViewById<Button>(R.id.forgotPwdBtn).setOnClickListener {
            val loginForgotPwdIntent = Intent(this, ForgotPassword::class.java)
            startActivity(loginForgotPwdIntent)
        }

        // Button
        // Account Login
        findViewById<Button>(R.id.acctLoginBtn).setOnClickListener {
            userEmail = findViewById<EditText>(R.id.loginEmail).text.toString()
            userPassword = findViewById<EditText>(R.id.loginPassword).text.toString()
            if (userEmail == "" || userPassword == "") {
                Toast.makeText(baseContext, "Missing User Login Info",
                    Toast.LENGTH_SHORT).show()
            } else
                login(userEmail, userPassword)
        }
    }

    private fun login(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    val user = FirebaseAuth.getInstance().currentUser
                    Toast.makeText(baseContext, "Login Successful. Choose your Story!",
                        Toast.LENGTH_SHORT).show()
                    updateUI(user)

                    val returnToMainIntent = Intent(this, MainActivity::class.java)
                    startActivity(returnToMainIntent)
                } else {
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val userName = user.displayName
            val userEmail = user.email
        }
    }
}