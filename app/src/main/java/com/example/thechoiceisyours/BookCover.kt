package com.example.thechoiceisyours

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class BookCover : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_cover)

        // Button
        // Navigate to Vol 2 Activity: activity_vol2_scrolling.xml & Vol2ScrollingActivity.kt
        findViewById<Button>(R.id.startBookBtn).setOnClickListener {
            val bookCoverToVol2Intent = Intent(this, Vol2ScrollingActivity::class.java)
            startActivity(bookCoverToVol2Intent)
        }
    }
}