package com.example.thechoiceisyours

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Button
        // Navigate to Vol 1 Cover Activity: activity_book_cover.xml & BookCover.kt
        findViewById<ImageButton>(R.id.vol1).setOnClickListener {
            val mainToBook1CoverIntent = Intent(this, StoryProgression::class.java)
            startActivity(mainToBook1CoverIntent)
        }

        // Button
        // Navigate to Vol 2 Cover Activity: activity_book_cover.xml & BookCover.kt
        findViewById<ImageButton>(R.id.vol2).setOnClickListener {
            val mainToBookCoverIntent = Intent(this, BookCover::class.java)
            startActivity(mainToBookCoverIntent)
        }
    }
}