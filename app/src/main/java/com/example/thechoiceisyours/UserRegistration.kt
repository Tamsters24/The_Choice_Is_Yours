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

class UserRegistration : AppCompatActivity() {
    private var newUserEmail: String = ""
    private var newUserPassword1: String = ""
    private var newUserPassword2: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_registration)

        FirebaseApp.initializeApp(this)

        // Image Button
        // Navigate to Main Activity Screen: activity_main.xml & MainActivity.kt
        findViewById<ImageButton>(R.id.registrationToMainBtn).setOnClickListener {
            val newAcctToLoginIntent = Intent(this, MainActivity::class.java)
            startActivity(newAcctToLoginIntent)
        }

        // Button
        // Complete New User Account Registration
        findViewById<Button>(R.id.completeRegBtn).setOnClickListener {
            newUserEmail = findViewById<EditText>(R.id.newAcctEmail).text.toString()
            newUserPassword1 = findViewById<EditText>(R.id.newAcctPW1).text.toString()
            newUserPassword2 = findViewById<EditText>(R.id.newAcctPW2).text.toString()
            // Check that passwords match
            if (newUserPassword1 != newUserPassword2) {
                Toast.makeText(baseContext, "passwords do not match, try again",
                    Toast.LENGTH_SHORT).show()
            } else if (newUserEmail == "" || newUserPassword1 == "") {
                Toast.makeText(baseContext, "Missing User Registration Info",
                    Toast.LENGTH_SHORT).show()
            } else {
                createAccount(newUserEmail, newUserPassword1)
            }
        }
    }

    private fun createAccount(newEmail: String, newPassword: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(newEmail, newPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val newUser = FirebaseAuth.getInstance().currentUser
                    Toast.makeText(baseContext, "Registration Successful!",
                        Toast.LENGTH_SHORT).show()
                    registerUpdateUI(newUser)

                    // For new user (UID) populate Chapter Nodes for Story Progression Map
                    // See Class ResetChaptersVisited, function resetNodesVisited()
                    ResetChaptersVisited.resetNodesVisited(this, "vol1")
                    ResetChaptersVisited.resetNodesVisited(this, "vol2")
                    ResetChaptersVisited.resetNodesVisited(this, "vol3")

                    // Return to Main Activity
                    val backToMainIntent = Intent(this, MainActivity::class.java)
                    startActivity(backToMainIntent)
                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Registration failed.",
                        Toast.LENGTH_SHORT).show()
                    registerUpdateUI(null)
                }
            }
    }

    private fun registerUpdateUI(user: FirebaseUser?) {
        if (user != null) {
            val userName = user.displayName
            val userEmail = user.email
        }
    }

}