package com.example.thechoiceisyours

import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.thechoiceisyours.databinding.ActivityBookScrollingBinding
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class BookScrollingActivity : AppCompatActivity() {
    private val storyLines = mutableListOf<String>()
    private var currentPart = 1
    private var currentChoice = "a"
    private var theEnd = false

    private lateinit var binding: ActivityBookScrollingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title

        getStory()
        readStory()
    }

    // Retrieve the Story from assets
    private fun getStory() {
        val inStream = assets.open("vol2_files/vol2_journey_under_the_sea.txt")

        // Read each line of the story
        val bufferedReader = BufferedReader(InputStreamReader(inStream))
        var line = bufferedReader.readLine()
        while (line != null) {
            storyLines.add(line)
            line = bufferedReader.readLine()
        }
        bufferedReader.close()
    }

    private fun readStory() {
        Toast.makeText(baseContext, "Part $currentPart", Toast.LENGTH_SHORT).show()

        // Display the current chapter image if one exists
        val filteredImage = storyLines.filter { it.contains("p$currentPart$currentChoice") }
        if (filteredImage.isNotEmpty()) {
            var chapterImageString = filteredImage[0]
            chapterImageString = chapterImageString.substring(11)
            displayImage(chapterImageString)
        }

        // Display the narrative of the current chapter
        displayChapter("Part.$currentPart$currentChoice")

        // Adjust the Choice Buttons display according to the number of choices available
        val choices = getNextChoices("$currentPart$currentChoice")
        displayButtons(choices)

        currentPart += 1
    }

    // Display image for current chapter
    private fun displayImage(imageName: String) {
        val imageInputStream: InputStream = assets.open(imageName)
        val image = Drawable.createFromStream(imageInputStream, null)
        val partImage = findViewById<ImageView>(R.id.pageImage)
        partImage.setImageDrawable(image)

        //partImage.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        //partImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    // Determine the current Chapter and Display Chapter contents and image
    private fun displayChapter(chapter: String) {
        // Current chapter narrative
        val filteredChapter = storyLines.filter { it.contains(chapter) }
        var storyChapter = filteredChapter[0]
        storyChapter = storyChapter.substring(9)
        storyChapter = storyChapter.replace("\\n", "\n\t\t\t")

        // Choice dialog
        var option1: String
        var option2: String
        val nextPart = currentPart + 1
        val nextChoices = getNextChoices("$currentPart$currentChoice")

        // If the chapter is not THE END, display choices.
        if (nextChoices[0] == "THE END") {
            option1 = ""
            option2 = "THE END"
        } else if (nextChoices[0].length == 2) {
            option1 = ""
            option2 = "Click to proceed"
        } else {
            // Choice A text
            var choice1Filter = "Choice.$currentPart$currentChoice.$nextPart"
            choice1Filter += nextChoices[0]
            val filteredOption1 = storyLines.filter { it.contains(choice1Filter) }
            option1 = filteredOption1[0]
            option1 = option1.substring(13)
            option1 = "Choice 1: $option1"

            // Choice B text
            var choice2Filter = "Choice.$currentPart$currentChoice.$nextPart"
            choice2Filter += nextChoices[1]
            val filteredOption2 = storyLines.filter { it.contains(choice2Filter) }
            option2 = filteredOption2[0]
            option2 = option2.substring(13)
            option2 = "Choice 2: $option2"
        }

        // Display text
        val chapterDisplay = findViewById<TextView>(R.id.chapterContents)
        val storyPage = SpannableString("$storyChapter\n\n$option1\n\n$option2")

        // Italicize all strings "Seeker"
        /*val seekerPattern = Pattern.compile("Seeker")
        val seekerMatcher = seekerPattern.matcher(storyChapter)
        while (seekerMatcher.find()) {
            val seekerStringStart = seekerMatcher.start()
            val seekerStringEnd = seekerMatcher.end()
            storyPage.setSpan(
                StyleSpan(Typeface.ITALIC), seekerStringStart, seekerStringEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }*/

        // Italicize all strings "Maray"
        /*val marayPattern = Pattern.compile("Maray")
        val marayMatcher = marayPattern.matcher(storyChapter)
        while (marayMatcher.find()) {
            val marayStringStart = marayMatcher.start()
            val marayStringEnd = marayMatcher.end()
            storyPage.setSpan(
                StyleSpan(Typeface.ITALIC), marayStringStart, marayStringEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }*/

        // Italicize option1 line if there are 2 choices
        if (option1 != "") {
            val option1StartIndex = storyPage.indexOf(option1)
            val option1EndIndex = option1StartIndex + option1.length
            val option1Choice1EndIndex = option1StartIndex + "Choice 1:".length
            val option1LineStartIndex = option1Choice1EndIndex + 1
            storyPage.setSpan(
                StyleSpan(Typeface.BOLD_ITALIC), option1StartIndex, option1Choice1EndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            storyPage.setSpan(
                StyleSpan(Typeface.ITALIC), option1LineStartIndex, option1EndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }


        // Italicize option2 line if there are 2 choices
        if (option2 != "THE END" && option2 != "Click to proceed") {
            val option2StartIndex = storyPage.indexOf(option2)
            val option2EndIndex = option2StartIndex + option2.length
            val option2Choice2EndIndex = option2StartIndex + "Choice 2:".length
            val option2LineStartIndex = option2Choice2EndIndex + 1
            storyPage.setSpan(
                StyleSpan(Typeface.BOLD_ITALIC), option2StartIndex, option2Choice2EndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            storyPage.setSpan(
                StyleSpan(Typeface.ITALIC), option2LineStartIndex, option2EndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        // Boldface option2 line if it is "THE END" or "Click to proceed"
        else {
            val option2StartIndex = storyPage.indexOf(option2)
            val option2EndIndex = option2StartIndex + option2.length
            storyPage.setSpan(
                StyleSpan(Typeface.BOLD), option2StartIndex, option2EndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        chapterDisplay.text = storyPage

        // Justify the TextView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chapterDisplay.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
        }

        // Scroll back to the top of the screen
        val storyView = findViewById<NestedScrollView>(R.id.storyLayout)
        storyView.smoothScrollTo(0,5,1500)
    }

    private fun displayButtons(choiceList: List<String>) {
        val option1Btn: ImageButton = findViewById(R.id.option1)
        val option2Btn: ImageButton = findViewById(R.id.option2)
        val option3Btn: ImageButton = findViewById(R.id.option3)

        when (choiceList.size) {
            1 -> {  // Occurs when there's no choice, which is either THE END
                // of the current story or to continue to a pre-designated chapter

                // Hide left and right option buttons, display center option button
                option1Btn.visibility = View.GONE
                option2Btn.visibility = View.GONE
                option3Btn.visibility = View.VISIBLE

                displayChapter("Part.$currentPart$currentChoice")

                if (choiceList[0] == "THE END") {   // Display last chapter, prompt to try different path,
                    theEnd = true
                    Toast.makeText(baseContext, "Click to try a different path", Toast.LENGTH_LONG).show()
                    option3Btn.setOnClickListener {
                        val theEndToBookCoverIntent = Intent(this, BookCover::class.java)
                        startActivity(theEndToBookCoverIntent)
                    }
                } else {  // Click on option button 3 to continue to assigned chapter
                    val listValue = choiceList[0]

                    // Determine the Int value for the Part to forward to. Decrement by 1 (readStory increments by 1).
                    currentPart = listValue.takeWhile { it.isDigit() }.toIntOrNull()!!
                    currentPart -= 1
                    // Determine the choice for the associated part
                    currentChoice = listValue[1].toString()

                    // Move forward to the next section after clicking option button
                    option3Btn.setOnClickListener {
                        readStory()
                    }
                }
            }

            2 -> {  // The most frequent occurrence. There are 2 choices for the reader

                // Hide center option button, display left and right option buttons
                option1Btn.visibility = View.VISIBLE
                option2Btn.visibility = View.VISIBLE
                option3Btn.visibility = View.GONE

                // Left button proceeds to first choice
                option1Btn.setOnClickListener {
                    currentChoice = choiceList[0]
                    readStory()
                }

                // Right button proceeds to next choice
                option2Btn.setOnClickListener {
                    currentChoice = choiceList[1]
                    readStory()
                }
            }

            3 -> {
                println("dummy2")
            }
        }
    }

    private fun getNextChoices(currentChoice: String): List<String> {
        return when (currentChoice) {
            "1a" -> listOf("a", "b")

            "2a" -> listOf("a", "b")
            "2b" -> listOf("c", "d")

            "3a" -> listOf("a", "b")
            "3b" -> listOf("c", "d")
            "3c" -> listOf("e", "f")
            "3d" -> listOf("g", "h")

            "4a" -> listOf("a", "b")
            "4b" -> listOf("c", "d")
            "4c" -> listOf("THE END")
            "4d" -> listOf("THE END")
            "4e" -> listOf("e", "f")
            "4f" -> listOf("g", "h")
            "4g" -> listOf("i", "j")
            "4h" -> listOf("k", "l")

            "5a" -> listOf("THE END")
            "5b" -> listOf("a", "b")
            "5c" -> listOf("c", "d")
            "5d" -> listOf("THE END")
            "5e" -> listOf("2a")
            "5f" -> listOf("e", "f")
            "5g" -> listOf("THE END")
            "5h" -> listOf("g", "h")
            "5i" -> listOf("i", "j")
            "5j" -> listOf("THE END")
            "5k" -> listOf("THE END")
            "5l" -> listOf("k", "l")

            "6a" -> listOf("a", "b")
            "6b" -> listOf("6c")
            "6c" -> listOf("3d")
            "6d" -> listOf("THE END")
            "6e" -> listOf("c", "d")
            "6f" -> listOf("e", "f")
            "6g" -> listOf("g", "h")
            "6h" -> listOf("THE END")
            "6i" -> listOf("i", "j")
            "6j" -> listOf("k", "l")
            "6k" -> listOf("m", "n")
            "6l" -> listOf("2a")

            "7a" -> listOf("a", "b")
            "7b" -> listOf("5a")
            "7c" -> listOf("c", "d")
            "7d" -> listOf("e", "f")
            "7e" -> listOf("g", "h")
            "7f" -> listOf("i", "j")
            "7g" -> listOf("k", "l")
            "7h" -> listOf("m", "n")
            "7i" -> listOf("o", "p")
            "7j" -> listOf("q", "r")
            "7k" -> listOf("7l", "8t", "8u")
            "7l" -> listOf("THE END")
            "7m" -> listOf("THE END")
            "7n" -> listOf("THE END")

            "8a" -> listOf("a", "b")
            "8b" -> listOf("THE END")
            "8c" -> listOf("c", "d")
            "8d" -> listOf("THE END")
            "8e" -> listOf("e", "f")
            "8f" -> listOf("THE END")
            "8g" -> listOf("2a")
            "8h" -> listOf("THE END")
            "8i" -> listOf("g", "h")
            "8j" -> listOf("i", "j")
            "8k" -> listOf("THE END")
            "8l" -> listOf("THE END")
            "8m" -> listOf("THE END")
            "8n" -> listOf("7e")
            "8o" -> listOf("THE END")
            "8p" -> listOf("k", "l")
            "8q" -> listOf("m", "n")
            "8r" -> listOf("THE END")
            "8s" -> listOf("THE END")
            "8t" -> listOf("THE END")

            "9a" -> listOf("THE END")
            "9b" -> listOf("a", "b")
            "9c" -> listOf("c", "d")
            "9d" -> listOf("e", "f")
            "9e" -> listOf("THE END")
            "9f" -> listOf("g", "h")
            "9g" -> listOf("THE END")
            "9h" -> listOf("THE END")
            "9i" -> listOf("THE END")
            "9j" -> listOf("7c")
            "9k" -> listOf("THE END")
            "9l" -> listOf("THE END")
            "9m" -> listOf("THE END")
            "9n" -> listOf("THE END")

            "10a" -> listOf("THE END")
            "10b" -> listOf("THE END")
            "10c" -> listOf("THE END")
            "10d" -> listOf("THE END")
            "10e" -> listOf("THE END")
            "10f" -> listOf("THE END")
            "10g" -> listOf("THE END")
            "10h" -> listOf("THE END")

            else -> emptyList()
        }
    }
}