package com.example.thechoiceisyours

import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class BookScrollingActivity : AppCompatActivity() {
    private val storyLines = mutableListOf<String>()
    private var currentPart = 1
    private var currentChoice = "a"
    private var theEnd = false
    private var assetsDirectory = ""
    private var choiceMap = mutableMapOf<String, List<String>>()
    private var nodesVisitedDB = ""
    private var bookAssets = ""

    private lateinit var binding: ActivityBookScrollingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menu options
        binding = ActivityBookScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title

        // Retrieve Book Contents using the Extra put in BookCover.kt
        bookAssets = intent.getStringExtra("assetsFolder").toString()

        // Concatenate name for directory and book content .txt files
        assetsDirectory = bookAssets + "_files/"
        val bookFile = bookAssets + "_contents.txt"
        val nextChoicesFile = bookAssets + "_nextChoices.txt"
        val bookContents = assetsDirectory + bookFile
        val nextChoicesContents = assetsDirectory + nextChoicesFile
        nodesVisitedDB = bookAssets + "NodesVisited"

        // Open the book, set the display, and update the visited chapters when accessed
        getStory(bookContents)
        choiceMap = setChoiceMap(nextChoicesContents)
        storyDisplay()
    }

    // Retrieve the Story from assets
    private fun getStory(bookContents: String) {
        val storyInStream = assets.open(bookContents)

        // Read each line of the story
        val bufferedStoryReader = BufferedReader(InputStreamReader(storyInStream))
        var line = bufferedStoryReader.readLine()
        while (line != null) {
            storyLines.add(line)
            line = bufferedStoryReader.readLine()
        }
        bufferedStoryReader.close()
    }

    // Set the views for the display, including images, narrative, and choice buttons
    private fun storyDisplay() {
        Toast.makeText(baseContext, "Part $currentPart", Toast.LENGTH_SHORT).show()

        // Access the Firebase database for Story Progress graph
        // Toggled the current Part.Choice "visited" to true when accessed
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val userId = user?.uid.toString()
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val userRef = usersRef.child(userId)
        if (currentPart > 1) { // First chapter is always considered visited
            val visitedChapter = userRef.child(nodesVisitedDB).
            child("$currentPart$currentChoice")
            visitedChapter.setValue(true)
        }

        // Update the current chapter image if one exists
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
        for (choice in choices)
            Toast.makeText(baseContext, choice, Toast.LENGTH_SHORT).show()
        displayButtons(choices)

        currentPart += 1  // Increment to the next chapter
    }

    // Set image for current chapter
    private fun displayImage(imageName: String) {
        val imageInputStream: InputStream = assets.open(imageName)
        val image = Drawable.createFromStream(imageInputStream, null)
        val partImage = findViewById<ImageView>(R.id.pageImage)
        partImage.setImageDrawable(image)
    }

    // Set the current Chapter and Display Chapter contents
    private fun displayChapter(chapter: String) {
        // Current chapter narrative
        val filteredChapter = storyLines.filter { it.contains(chapter) }
        var storyChapter = filteredChapter[0]
        storyChapter = storyChapter.substring(9)
        storyChapter = storyChapter.replace("\\n", "\n\t\t\t")

        // Choice dialog
        val options = mutableListOf<String>()
        val nextChoices = getNextChoices("$currentPart$currentChoice")
        var choiceNumber = 1

        // If the chapter is THE END, display chapter and "THE END".
        if (nextChoices[0] == "END") {
            options.add("")
            options.add("THE END")
        }
        // If the chapter is not THE END but has no choices, prompt to "proceed"
        else if (nextChoices.size == 1 && nextChoices[0] != "END") {
            options.add("")
            options.add("Click to proceed")
        }
        // Otherwise, there are 2 choices or more choices. Display choice text.
        if (nextChoices.size > 1) {
            print("inside nextChoices > 1")
            for (choice in nextChoices) {
                val choiceFilter = "Choice.$currentPart$currentChoice.$choice"
                val filteredOption = storyLines.filter { it.contains(choiceFilter) }
                var optionString = filteredOption.toString()
                optionString = optionString.substring(13)
                optionString = "Choice $choiceNumber: $optionString"
                optionString = optionString.dropLast(1)
                options.add(optionString)
                choiceNumber++
            }
        }

        // Gather text strings for display
        val chapterDisplay = findViewById<TextView>(R.id.chapterContents)
        var storyPage = SpannableString("$storyChapter\n\n${options[0]}\n\n${options[1]}")
        when (options.size) {
            2 -> {
                storyPage = SpannableString("$storyChapter\n\n${options[0]}\n\n${options[1]}")
            }
            3 -> {
                storyPage = SpannableString("$storyChapter\n\n${options[0]}\n\n" +
                        "${options[1]}\n\n${options[2]}")
            }
            4 -> {
                storyPage = SpannableString("$storyChapter\n\n${options[0]}\n\n" +
                        "${options[1]}\n\n${options[2]}\n\n${options[3]}")
            }
        }

        // Text styling
        for (i in 0 until options.size) {
            // Italicize option lines if there are 2 or more choices
            if (options[1] != "THE END" && options[1] != "Click to proceed") {
                val optionStartIndex = storyPage.indexOf(options[i])
                val optionEndIndex = optionStartIndex + options[i].length
                val choiceValue = i + 1
                val optionChoiceEndIndex = optionStartIndex + "Choice $choiceValue:".length
                val optionLineStartIndex = optionChoiceEndIndex + 1
                storyPage.setSpan(
                    StyleSpan(Typeface.BOLD_ITALIC), optionStartIndex, optionChoiceEndIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                storyPage.setSpan(
                    StyleSpan(Typeface.ITALIC), optionLineStartIndex, optionEndIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            // Boldface option2 line if it is "THE END" or "Click to proceed"
            else {
                val theEndStartIndex = storyPage.indexOf(options[1])
                val theEndEndIndex = theEndStartIndex + options[1].length
                storyPage.setSpan(
                    StyleSpan(Typeface.BOLD), theEndStartIndex, theEndEndIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        chapterDisplay.text = storyPage

        // Scroll back to the top of the screen
        val storyView = findViewById<NestedScrollView>(R.id.storyLayout)
        storyView.smoothScrollTo(0,5,1500)
    }

    // Switch visibility of choice Buttons according to choices available
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

                if (choiceList[0] == "END") {   // Display last chapter, prompt to try different path,
                    theEnd = true
                    Toast.makeText(baseContext, "Click to try a different path", Toast.LENGTH_LONG).show()
                    option3Btn.setOnClickListener {
                        val theEndToBookCoverIntent = Intent(this, BookCover::class.java)
                        theEndToBookCoverIntent.putExtra("assetsFolder", bookAssets)
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
                        storyDisplay()
                    }
                }
            }

            2 -> {  // The most frequent occurrence. There are 2 choices for the reader

                // Hide center option button, display left and right option buttons
                option1Btn.visibility = View.VISIBLE
                option2Btn.visibility = View.VISIBLE
                option3Btn.visibility = View.GONE

                // Left button proceeds to first choice. Choices are 2 characters. Retain the second char.
                option1Btn.setOnClickListener {
                    currentChoice = choiceList[0]
                    currentChoice = currentChoice.substring(1)
                    storyDisplay()
                }

                // Right button proceeds to next choice
                option2Btn.setOnClickListener {
                    currentChoice = choiceList[1]
                    currentChoice = currentChoice.substring(1)
                    storyDisplay()
                }
            }

            3 -> {
                // Display left, right, and center option buttons
                option1Btn.visibility = View.VISIBLE
                option2Btn.visibility = View.VISIBLE
                option3Btn.visibility = View.VISIBLE

                // Rearrange image used for center button as "2" and right button as "3"
                // Right button
                option2Btn.setImageResource(R.drawable.ic_twotone_looks_3_24)
                // Center button
                option3Btn.setImageResource(R.drawable.ic_twotone_looks_two_24)

                // Left button proceeds to choice 1. Choices are 2 characters. Retain the second char.
                option1Btn.setOnClickListener {
                    currentChoice = choiceList[0]
                    currentChoice = currentChoice.substring(1)
                    storyDisplay()
                }

                // Right button proceeds to choice 3
                option2Btn.setOnClickListener {
                    currentChoice = choiceList[2]
                    currentChoice = currentChoice.substring(1)
                    storyDisplay()
                }

                // Center button proceeds to next choice
                option3Btn.setOnClickListener {
                    currentChoice = choiceList[1]
                    currentChoice = currentChoice.substring(1)
                    storyDisplay()
                }
            }
        }
    }

    private fun setChoiceMap(nextChoicesFile: String): MutableMap<String, List<String>> {
        val choiceMapSet = mutableMapOf<String, List<String>>()
        val inputStream: InputStream = assets.open(nextChoicesFile)
        val nextChoiceReader = BufferedReader(InputStreamReader(inputStream))
        var choiceLines = nextChoiceReader.readLine()

        while (choiceLines != null) {
            val chapterParts = choiceLines.split(" ")
            val key = chapterParts[0]
            val values = chapterParts.subList(1,chapterParts.size)
            choiceMapSet[key] = values
            choiceLines = nextChoiceReader.readLine()
        }
        nextChoiceReader.close()
        return choiceMapSet
    }

    private fun getNextChoices(currentChoice: String): List<String> {
        return choiceMap[currentChoice] ?: emptyList()
    }
}