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

        // Button
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

                    createStoryNodesVisited ()

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

        // Create a maps of the boolean values for vol1
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

        /*val vol1BooleanValues = mutableMapOf(
            "1" to true,
            "2a" to false, "2b" to false,
            "3a" to false, "3b" to false, "3c" to false, "3d" to false,
            "4a" to false, "4b" to false, "4c" to false, "4d" to false, "4e" to false,
            "4f" to false, "4g" to false, "4h" to false, "4i" to false,
            "5a" to false, "5b" to false, "5c" to false, "5d" to false, "5e" to false, "5f" to false,
            "5g" to false, "5h" to false, "5i" to false, "5j" to false, "5k" to false, "5l" to false,
            "5m" to false, "5n" to false, "5o" to false, "5p" to false, "5q" to false,
            "6a" to false, "6b" to false, "6c" to false, "6d" to false, "6e" to false, "6f" to false,
            "6g" to false, "6h" to false, "6i" to false, "6j" to false, "6k" to false, "6l" to false,
            "6m" to false, "6n" to false, "6o" to false, "6p" to false, "6q" to false,
            "6r" to false, "6s" to false, "6t" to false, "6u" to false, "6v" to false,
            "7a" to false, "7b" to false, "7c" to false, "7d" to false, "7e" to false,
            "7f" to false, "7g" to false, "7h" to false, "7i" to false, "7j" to false,
            "7k" to false, "7l" to false, "7m" to false, "7n" to false,
            "8a" to false, "8b" to false, "8c" to false, "8d" to false, "8e" to false,
            "8f" to false, "8g" to false, "8h" to false, "8i" to false, "8j" to false,
            "8k" to false, "8l" to false, "8m" to false, "8n" to false,
            "9a" to false, "9b" to false,
            "10a" to false, "10b" to false
        )*/

        // Create a maps of the boolean values for vol2
        // Create a maps of the boolean values for vol1
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

        /*val vol2BooleanValues = mutableMapOf(
            "1" to true,
            "2a" to false, "2b" to false,
            "3a" to false, "3b" to false, "3c" to false, "3d" to false,
            "4a" to false, "4b" to false, "4c" to false, "4d" to false,
            "4e" to false, "4f" to false, "4g" to false, "4h" to false,
            "5a" to false, "5b" to false, "5c" to false, "5d" to false,
            "5e" to false, "5f" to false, "5g" to false, "5h" to false,
            "5i" to false, "5j" to false, "5k" to false, "5l" to false,
            "6a" to false, "6b" to false, "6c" to false, "6d" to false,
            "6e" to false, "6f" to false, "6g" to false, "6h" to false,
            "6i" to false, "6j" to false, "6k" to false, "6l" to false,
            "7a" to false, "7b" to false, "7c" to false, "7d" to false, "7e" to false,
            "7f" to false, "7g" to false, "7h" to false, "7i" to false, "7j" to false,
            "7k" to false, "7l" to false, "7m" to false, "7n" to false,
            "8a" to false, "8b" to false, "8c" to false, "8d" to false, "8e" to false,
            "8f" to false, "8g" to false, "8h" to false, "8i" to false, "8j" to false,
            "8k" to false, "8l" to false, "8m" to false, "8n" to false, "8o" to false,
            "8p" to false, "8q" to false, "8r" to false, "8s" to false, "8t" to false,
            "9a" to false, "9b" to false, "9c" to false, "9d" to false, "9e" to false,
            "9f" to false, "9g" to false, "9h" to false, "9i" to false, "9j" to false,
            "9k" to false, "9l" to false, "9m" to false, "9n" to false, "9o" to false,
            "10a" to false, "10b" to false, "10c" to false, "10d" to false,
            "10e" to false, "10f" to false, "10g" to false, "10h" to false
        )*/

        // Write the boolean values to the database
        vol1NodesVisited.setValue(vol1BooleanValues).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Database write successful
                println("Database write successful")
            } else {
                // Handle database write error
                println("Database write failed")
            }
        }

        // Write the boolean values to the database
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