package com.example.thechoiceisyours

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

import java.io.BufferedReader
import java.io.InputStreamReader

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
                    createStoryNodesVisited ()

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

    private fun createStoryNodesVisited () {
        val firebaseAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()

        // Determine the user's UID
        val user = firebaseAuth.currentUser
        val userId = user?.uid.toString()

        // Get a reference to the "users" node
        val usersRef = database.getReference("users")

        // Create a new user node with UID
        val userRef = usersRef.child(userId)
        val vol1NodesVisited = userRef.child("vol1NodesVisited")
        val vol2NodesVisited = userRef.child("vol2NodesVisited")

        // Create a map of the boolean values for vol1
        val vol1Nodes = assets.open("vol1_files/vol1NodeNames.txt")
        val vol1NodesReader = BufferedReader(InputStreamReader(vol1Nodes))
        var vol1Node = vol1NodesReader.readLine()
        val vol1BooleanValues = mutableMapOf(vol1Node to true)
        vol1Node = vol1NodesReader.readLine()
        while (vol1Node != null) {
            vol1BooleanValues[vol1Node] = false
            vol1Node = vol1NodesReader.readLine()
        }
        vol1NodesReader.close()

        // Create a map of the boolean values for vol2
        val vol2Nodes = assets.open("vol2_files/vol2NodeNames.txt")
        val vol2NodesReader = BufferedReader(InputStreamReader(vol2Nodes))
        var vol2Node = vol2NodesReader.readLine()
        val vol2BooleanValues = mutableMapOf(vol2Node to true)
        vol2Node = vol2NodesReader.readLine()
        while (vol2Node != null) {
            vol2BooleanValues[vol2Node] = false
            vol2Node = vol2NodesReader.readLine()
        }
        vol2NodesReader.close()

        // Write the vol1 boolean values to the database
        vol1NodesVisited.setValue(vol1BooleanValues).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Database write successful
                println("Database write successful")
            } else {
                // Handle database write error
                println("Database write failed")
            }
        }

        // Write the vol2 boolean values to the database
        vol2NodesVisited.setValue(vol2BooleanValues).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Database write successful
                println("Database write successful")
            } else {
                // Handle database write error
                println("Database write failed")
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