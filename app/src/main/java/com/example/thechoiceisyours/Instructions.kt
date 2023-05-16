package com.example.thechoiceisyours

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView

@Suppress("DEPRECATION")
class Instructions : AppCompatActivity() {
    private var instructionsPage = 1

    // Variables for Text Views
    private lateinit var instructionText1: TextView
    private lateinit var instructionText2: TextView
    private lateinit var instructionText3: TextView

    // Variables for Image Views
    private lateinit var instructionImage1: ImageView
    private lateinit var instructionImage2: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)

        // onCreate displays page 1 of the instructions.
        instructionText1 = findViewById(R.id.instructionA)
        instructionText2 = findViewById(R.id.instructionB)
        instructionText3 = findViewById(R.id.instructionC)
        instructionImage1 = findViewById(R.id.instructionsImage1)
        instructionImage2 = findViewById(R.id.instructionsImage2)

        // 3 Buttons are available on the screen.

        // Back Button
        findViewById<ImageButton>(R.id.back_Btn).setOnClickListener {
            if (instructionsPage > 1) {
                instructionsPage--
                buttonPress(instructionsPage)
            } else { /* Do nothing */}
        }

        // Next Button
        findViewById<ImageButton>(R.id.next_Btn).setOnClickListener {
            if (instructionsPage < 5) {
                instructionsPage++
                buttonPress(instructionsPage)
            } else {
                // Upon click, from page 5, return to the Main Activity
                val pg5ToMainIntent = Intent(this, MainActivity::class.java)
                startActivity(pg5ToMainIntent)
            }
        }

        // For the Home button, return to main activity.
        // Main Page Button
        findViewById<ImageButton>(R.id.instrToMain_Btn).setOnClickListener {
            val instructionsToMainIntent = Intent(this, MainActivity::class.java)
            startActivity(instructionsToMainIntent)
        }
    }

    //For the Back and Next buttons, update instructions and images upon button click.
    private fun buttonPress(page: Int) {
        when (page) {
            1 -> {
                updateInstructions(R.string.instructions1,
                    R.string.instructions2, R.string.instructions3)
                updateInstructImage(R.drawable.instruction_pic1, R.drawable.instruction_pic2)
                scrollToTop()
            }
            2 -> {
                updateInstructions(R.string.instructions4,
                    R.string.instructions5, R.string.instructions6)
                updateInstructImage(R.drawable.instruction_pic3, R.drawable.instruction_pic4)
                scrollToTop()
            }
            3 -> {
                updateInstructions(R.string.instructions7,
                    R.string.instructions8, R.string.instructions9)
                updateInstructImage(R.drawable.instruction_pic5, R.drawable.instruction_pic6)
                scrollToTop()
            }
            4 -> {
                updateInstructions(R.string.instructions10,
                    R.string.instructions11, R.string.instructions12)
                updateInstructImage(R.drawable.instruction_pic7, R.drawable.instruction_pic8)
                scrollToTop()
            }
            5 -> {
                updateInstructions(R.string.instructions13,
                    R.string.instructions14, R.string.instructions15)
                updateInstructImage(R.drawable.registration_ex, R.drawable.password_ex)
                scrollToTop()
            }
        }
    }

    // Upon click, update instructions for next page
    private fun updateInstructions(text1: Int, text2: Int, text3: Int) {
        instructionText1.setText(text1)
        instructionText2.setText(text2)
        instructionText3.setText(text3)
    }

    // Upon click for next page update images, resize to content, and adjust layout
    private fun updateInstructImage(image1: Int, image2: Int) {
        val instructImage1 = resources.getDrawable(image1)
        instructionImage1.setImageDrawable(instructImage1)
        instructionImage1.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        val image1params = instructionImage1.layoutParams as RelativeLayout.LayoutParams
        image1params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        instructionImage1.layoutParams = image1params

        val instructImage2 = resources.getDrawable(image2)
        instructionImage2.setImageDrawable(instructImage2)
        instructionImage2.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        val image2params = instructionImage2.layoutParams as RelativeLayout.LayoutParams
        image2params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        instructionImage2.layoutParams = image2params
    }

    // Scroll to top of view
    private fun scrollToTop() {
        val instructionsView = findViewById<NestedScrollView>(R.id.instructionsLayout)
        instructionsView.smoothScrollTo(0,5,1500)
    }
}