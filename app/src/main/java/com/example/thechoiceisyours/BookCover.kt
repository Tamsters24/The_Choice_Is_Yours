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

        // Button
        // Navigate to Vol 2 Activity: activity_vol2_scrolling.xml & Vol2ScrollingActivity.kt
        findViewById<Button>(R.id.startBookBtn).setOnClickListener {
            val bookCoverToVol2Intent = Intent(this, BookScrollingActivity::class.java)
            startActivity(bookCoverToVol2Intent)
        }

        val coverInputStream: InputStream = assets.open("vol2_images/vol2_cover.jpg")
        val volumeCover = Drawable.createFromStream(coverInputStream, null)

        val volumeImage = findViewById<ImageView>(R.id.coverPage)
        volumeImage.setImageDrawable(volumeCover)
        volumeImage.layout(600,0,600,600)
    }
}