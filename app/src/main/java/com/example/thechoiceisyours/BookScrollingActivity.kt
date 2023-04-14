package com.example.thechoiceisyours

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.thechoiceisyours.databinding.ActivityBookScrollingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        binding = ActivityBookScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title

        // Retrieve Book Contents using the Extra put in BookCover.kt
        bookAssets = intent.getStringExtra("assetsFolder").toString()
        // Concatenate name for direct43ory and book content .txt file
        assetsDirectory = bookAssets + "_files/"
        val bookFile = bookAssets + "_contents.txt"
        val nextChoicesFile = bookAssets + "_nextChoices.txt"
        val bookContents = assetsDirectory + bookFile
        val nextChoicesContents = assetsDirectory + nextChoicesFile
        nodesVisitedDB = bookAssets + "NodesVisited"

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

        // Access the Firebase database and toggle the current Part.Choice "visited" to true
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val userId = user?.uid.toString()
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val userRef = usersRef.child(userId)
        val visitedChapter = userRef.child(nodesVisitedDB).
                             child("$currentPart$currentChoice")
        if (currentPart > 1)  // First chapter is always considered visited
            visitedChapter.setValue(true)


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

    // Set image for current chapter
    private fun displayImage(imageName: String) {
        val imageInputStream: InputStream = assets.open(imageName)
        val image = Drawable.createFromStream(imageInputStream, null)
        val partImage = findViewById<ImageView>(R.id.pageImage)
        partImage.setImageDrawable(image)

        //partImage.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        //partImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    // Set the current Chapter and Display Chapter contents
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

        if (nextChoices[0] == "END") {  // If the chapter is THE END, display chapter and "THE END".
            option1 = ""
            option2 = "THE END"
        } else if (nextChoices[0].length == 2) {  // The chapter is not THE END, but has no choices.
            option1 = ""
            option2 = "Click to proceed"
        } else {  // There are 2 choices. Display choice text
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

                // Left button proceeds to first choice
                option1Btn.setOnClickListener {
                    currentChoice = choiceList[0]
                    storyDisplay()
                }

                // Right button proceeds to next choice
                option2Btn.setOnClickListener {
                    currentChoice = choiceList[1]
                    storyDisplay()
                }
            }

            3 -> {
                println("dummy2")
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