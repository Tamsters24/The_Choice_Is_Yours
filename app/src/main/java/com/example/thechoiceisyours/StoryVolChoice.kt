package com.example.thechoiceisyours

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class StoryVolChoice  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_vol_choice)

        // Image Button
        // Navigate to Main Activity: activity_main.xml & MainActivity.kt
        findViewById<ImageButton>(R.id.libraryToMain_btn).setOnClickListener {
            val libraryToMainIntent = Intent(this, MainActivity::class.java)
            startActivity(libraryToMainIntent)
        }

        // Navigate to Book Cover Activity: activity_book_cover.xml & BookCover.kt
        val storyChoiceToCoverIntent = Intent(this, BookCover::class.java)

        // Button 1: Volume 1 cover
        findViewById<Button>(R.id.vol1_btn).setOnClickListener {
            storyChoiceToCoverIntent.putExtra("assetsFolder", "vol1")
            startActivity(storyChoiceToCoverIntent)
        }

        // Button 2: Volume 2 cover
        findViewById<Button>(R.id.vol2_btn).setOnClickListener {
            storyChoiceToCoverIntent.putExtra("assetsFolder", "vol2")
            startActivity(storyChoiceToCoverIntent)
        }

        // Button 3: Volume 3 cover
        findViewById<Button>(R.id.vol3_btn).setOnClickListener {
            storyChoiceToCoverIntent.putExtra("assetsFolder", "vol3")
            startActivity(storyChoiceToCoverIntent)
        }

        // Button 4: Volume 4 cover
        findViewById<Button>(R.id.vol4_btn).setOnClickListener {
            storyChoiceToCoverIntent.putExtra("assetsFolder", "vol4")
            startActivity(storyChoiceToCoverIntent)
        }

        // Button 5: Volume 5 cover
        findViewById<Button>(R.id.vol5_btn).setOnClickListener {
            storyChoiceToCoverIntent.putExtra("assetsFolder", "vol5")
            startActivity(storyChoiceToCoverIntent)
        }
    }
}