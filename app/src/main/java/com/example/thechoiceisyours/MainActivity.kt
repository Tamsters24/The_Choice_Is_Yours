package com.example.thechoiceisyours

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Button
        // Navigate to StoryVolChoice Activity: activity_story_vol_choice.xml & StoryVolChoice.kt
        findViewById<Button>(R.id.mainToLibrary_btn).setOnClickListener {
            val mainToStoryChoiceIntent = Intent(this, StoryLibrary::class.java)
            startActivity(mainToStoryChoiceIntent)
        }

        // Button
        // Navigate to Login Activity: activity_user_login.xml & UserLogin.kt
        findViewById<Button>(R.id.mainToLogin_btn).setOnClickListener {
            val mainToLoginIntent = Intent(this, UserLogin::class.java)
            startActivity(mainToLoginIntent)
        }

        // Button
        // Navigate to Registration Activity: activity_user_registration.xml & UserRegistration.kt
        findViewById<Button>(R.id.MainToRegistration_btn).setOnClickListener {
            val mainToRegistrationIntent = Intent(this, UserRegistration::class.java)
            startActivity(mainToRegistrationIntent)
        }

        // Button
        // Navigate to Instructions Activity: activity_instructions.xml & Instructions.kt
        findViewById<Button>(R.id.MainToInstructions_btn).setOnClickListener {
            val mainToInstructionsIntent = Intent(this, Instructions::class.java)
            startActivity(mainToInstructionsIntent)
        }
    }
}