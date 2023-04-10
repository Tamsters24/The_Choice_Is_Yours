package com.example.thechoiceisyours

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import java.io.InputStream

class BookCover : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_cover)

        // Display Book Cover using the Extra put in StoryVolChoice.kt
        val bookAssets = intent.getStringExtra("assetsFolder").toString()
        // Concatenate name for directory and book cover .jpg file
        val assetsDirectory = bookAssets + "_files"
        val bookCoverName = bookAssets + "_cover.jpg"

        // Open file and set image
        val coverInputStream: InputStream = assets.open("$assetsDirectory/$bookCoverName")
        val volumeCover = Drawable.createFromStream(coverInputStream, null)
        val volumeImage = findViewById<ImageView>(R.id.coverPage)
        volumeImage.setImageDrawable(volumeCover)
        volumeImage.layout(600,0,600,600)


        // Button
        // Navigate to Book Scrolling Activity: activity_book_scrolling.xml & BookScrollingActivity.kt
        findViewById<Button>(R.id.startBookBtn).setOnClickListener {
            val bookCoverToBookScrollingIntent = Intent(this, BookScrollingActivity::class.java)
            bookCoverToBookScrollingIntent.putExtra("assetsFolder", bookAssets)
            startActivity(bookCoverToBookScrollingIntent)
        }

        // Button
        // Navigate to Story Progression Activity: activity_story_progression.xml & StoryProgression.kt
        findViewById<Button>(R.id.checkProgressBtn).setOnClickListener {
            val bookCoverToCheckProgressIntent = Intent(this, StoryProgression::class.java)
            bookCoverToCheckProgressIntent.putExtra("assetsFolder", bookAssets)
            startActivity(bookCoverToCheckProgressIntent)
        }
    }
}