package com.example.thechoiceisyours

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView

@Suppress("DEPRECATION")
class Instructions : AppCompatActivity() {
    private var instructionsPage = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)

        // Variables for Text and Image Views to be adjusted for each page
        val instructionText1 = findViewById<TextView>(R.id.instructionA)
        val instructionText2 = findViewById<TextView>(R.id.instructionB)
        val instructionText3 = findViewById<TextView>(R.id.instructionC)
        val instructionImage1 = findViewById<ImageView>(R.id.instructionsImage1)
        val instructionImage2 = findViewById<ImageView>(R.id.instructionsImage2)

        // Button
        // Move to the next page of instructions.
        findViewById<Button>(R.id.instrToNext_btn).setOnClickListener {
            when (instructionsPage) {
                1 -> {
                    // Upon click, from page 1, update instructions to page 2
                    instructionText1.setText(R.string.instructions4)
                    instructionText2.setText(R.string.instructions5)
                    instructionText3.setText(R.string.instructions6)

                    // Update images and resize to content, and adjust layout
                    val image1 = resources.getDrawable(R.drawable.book_cover_ex)
                    instructionImage1.setImageDrawable(image1)
                    instructionImage1.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    val image1params = instructionImage1.layoutParams as RelativeLayout.LayoutParams
                    image1params.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    instructionImage1.layoutParams = image1params

                    val image2 = resources.getDrawable(R.drawable.story_map_ex)
                    instructionImage2.setImageDrawable(image2)
                    instructionImage2.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    val image2params = instructionImage2.layoutParams as RelativeLayout.LayoutParams
                    image2params.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    instructionImage2.layoutParams = image2params

                    // Scroll to top of view
                    val instructionsView = findViewById<NestedScrollView>(R.id.instructionsLayout)
                    instructionsView.smoothScrollTo(0,5,1500)

                    // Increment page value
                    instructionsPage++
                }
                2 -> {
                    // Upon click, from page 2, update instructions to page 3
                    instructionText1.setText(R.string.instructions7)
                    instructionText2.setText(R.string.instructions8)
                    instructionText3.setText(R.string.instructions9)

                    val image1 = resources.getDrawable(R.drawable.registration_ex)
                    instructionImage1.setImageDrawable(image1)
                    instructionImage1.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    val image1params = instructionImage1.layoutParams as RelativeLayout.LayoutParams
                    image1params.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    instructionImage1.layoutParams = image1params

                    val image2 = resources.getDrawable(R.drawable.password_ex)
                    instructionImage2.setImageDrawable(image2)
                    instructionImage2.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    val image2params = instructionImage2.layoutParams as RelativeLayout.LayoutParams
                    image2params.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    instructionImage2.layoutParams = image2params

                    val instructionsView = findViewById<NestedScrollView>(R.id.instructionsLayout)
                    instructionsView.smoothScrollTo(0,5,1500)

                    instructionsPage++
                }
                else -> {
                    // Upon click, from page 3, return to the Main Activity
                    val instructionsToMainIntent = Intent(this, MainActivity::class.java)
                    startActivity(instructionsToMainIntent)
                }
            }
        }
    }
}