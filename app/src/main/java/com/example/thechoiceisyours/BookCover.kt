package com.example.thechoiceisyours

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class BookCover : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_cover)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Display Book Cover using the Extra put in StoryVolChoice.kt
        val bookAssets = intent.getStringExtra("assetsFolder").toString()

        // Concatenate name for directory and book cover .jpg file
        val assetsDirectory = bookAssets + "_files"
        val bookCoverName = bookAssets + "_cover.jpg"

        // Open file and set image/image layout
        val coverInputStream: InputStream = assets.open("$assetsDirectory/$bookCoverName")
        val volumeCover = Drawable.createFromStream(coverInputStream, null)
        val volumeImage = findViewById<ImageView>(R.id.coverPage)
        volumeImage.setImageDrawable(volumeCover)
        volumeImage.layout(600,0,600,600)


        // Button
        // Navigate to Book Scrolling Activity: activity_book_scrolling.xml & BookScrollingActivity.kt
        findViewById<Button>(R.id.startBook_btn).setOnClickListener {
            val bookCoverToBookScrollingIntent = Intent(this, BookScrollingActivity::class.java)
            bookCoverToBookScrollingIntent.putExtra("assetsFolder", bookAssets)
            startActivity(bookCoverToBookScrollingIntent)
        }

        // Button
        // Navigate to Library Activity: story_vol_choice.xml & StoryVolChoice.kt
        findViewById<Button>(R.id.checkProgress_btn).setOnClickListener {
            val bookCoverToLibraryIntent = Intent(this, StoryLibrary::class.java)
            startActivity(bookCoverToLibraryIntent)
        }
    }
}