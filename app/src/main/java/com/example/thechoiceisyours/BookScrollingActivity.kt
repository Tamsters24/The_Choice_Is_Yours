package com.example.thechoiceisyours

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.thechoiceisyours.databinding.ActivityBookScrollingBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class BookScrollingActivity : AppCompatActivity() {
    private var assetsDirectory = ""
    private var bookAssets = ""
    private val storyLines = mutableListOf<String>()
    private var choiceMap = mutableMapOf<String, List<String>>()
    private var currentChapter = "1a"

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    private var bookmark = ""
    private var nodesVisitedDB = ""

    private lateinit var binding: ActivityBookScrollingBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Retrieve Book Contents using the Extra put in BookCover.kt
        bookAssets = intent.getStringExtra("assetsFolder").toString()

        // Concatenate name for directory and book content .txt files
        assetsDirectory = bookAssets + "_files/"
        val bookFile = bookAssets + "_contents.txt"
        val bookContents = assetsDirectory + bookFile
        val nextChoicesFile = bookAssets + "_nextChoices.txt"
        val nextChoicesContents = assetsDirectory + nextChoicesFile

        // Concatenate name for Database References
        bookmark = bookAssets + "Bookmark"
        nodesVisitedDB = bookAssets + "NodesVisited"

        // Menu options
        binding = ActivityBookScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Navigation Drawer options
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        updateNavHeader(bookAssets)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@BookScrollingActivity, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> {
                        navigateToDrawerItem(it.itemId)
                    }
                    R.id.nav_fast_forward -> {
                        navigateToDrawerItem(it.itemId)
                    }
                    R.id.nav_rewind -> {
                        currentChapter = "1a"
                        storyDisplay(userRef, currentChapter)
                    }
                    R.id.nav_bookmark -> {
                        val bookMarkChapter = userRef.child(bookmark)
                        bookMarkChapter.setValue(currentChapter)
                    }
                    R.id.nav_story_map -> {
                        navigateToDrawerItem(it.itemId)
                    }
                    R.id.nav_exit -> {
                        val bookMarkChapter = userRef.child(bookmark)
                        bookMarkChapter.setValue(currentChapter)
                        navigateToDrawerItem(it.itemId)
                    }
                    R.id.nav_logout -> {
                        firebaseAuth.signOut()
                        Toast.makeText(baseContext, "Logout Successful", Toast.LENGTH_SHORT).show()
                    }
                    R.id.nav_close -> {
                        Toast.makeText(baseContext, "Goodbye", Toast.LENGTH_SHORT).show()
                        finishAffinity()
                    }
                    R.id.nav_delete -> {
                        val builder: AlertDialog.Builder =
                            AlertDialog.Builder(this@BookScrollingActivity)
                        builder.setMessage("Are you sure?")
                        builder.setPositiveButton("Yes") {dialog, which ->
                            ResetChaptersVisited.resetNodesVisited(this@BookScrollingActivity, bookAssets)
                            dialog.cancel()
                        }
                        builder.setNegativeButton("No") { dialog, which ->
                            dialog.cancel()
                        }
                        val dialog = builder.create()
                        dialog.show()
                    }
                }
                true
            }
        }

        // Access user Database to retrieve their bookmark. Begin story.
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val userId = user?.uid.toString()
        val database = FirebaseDatabase.getInstance()
        userRef = database.getReference("users").child(userId)

        // Open the book, set the display, and update the visited chapters when accessed
        getStory(bookContents)
        choiceMap = setChoiceMap(nextChoicesContents)
        storyDisplay(userRef, currentChapter)
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
    private fun storyDisplay(userRef: DatabaseReference, currentChapter: String) {
        Toast.makeText(baseContext, "Part $currentChapter", Toast.LENGTH_SHORT).show()

        // Access the Firebase database for Story Progress graph
        // Toggle the current Part.Choice "visited" to true when accessed
        if (currentChapter != "1a" && userRef.key != "null") { // First chapter is always considered visited
            val visitedChapter = userRef.child(nodesVisitedDB).child(currentChapter)
            visitedChapter.setValue(true)
        }

        // Update the current chapter image if one exists
        val filteredImage = storyLines.filter { it.contains("p$currentChapter") }
        if (filteredImage.isNotEmpty()) {
            var chapterImageString = filteredImage[0]
            chapterImageString = chapterImageString.substring(11)
            displayImage(chapterImageString)
        }

        // Display the narrative of the current chapter
        displayChapter("Part.$currentChapter")

        // Adjust the Choice Buttons display according to the number of choices available
        val choices = getNextChoices(currentChapter)
        displayButtons(userRef, choices)

        var choiceMsg = "Choices: "
        for (choice in choices)
            choiceMsg += "$choice, "
        Toast.makeText(baseContext, choiceMsg, Toast.LENGTH_SHORT).show()
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
        val nextChoices = getNextChoices(currentChapter)
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
            for (choice in nextChoices) {
                val choiceFilter = "Choice.$currentChapter.$choice"
                val filteredOption = storyLines.filter { it.contains(choiceFilter) }
                var optionString = filteredOption.toString()
                optionString = optionString.substring(13)
                optionString = "Choice $choiceNumber: $optionString"
                optionString = optionString.dropLast(1)
                optionString = optionString.replace("\\n", "\n")
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
    private fun displayButtons(userRef: DatabaseReference, choiceList: List<String>) {
        val option1Btn: ImageButton = findViewById(R.id.option1)
        val option2Btn: ImageButton = findViewById(R.id.option2)
        val option3Btn: ImageButton = findViewById(R.id.option3)
        val option4Btn: ImageButton = findViewById(R.id.option4)

        when (choiceList.size) {
            1 -> {  // Occurs when there's no choice, which is either THE END
                // of the current story or to continue to a pre-designated chapter

                // Hide left and right option buttons, display center option button
                option1Btn.visibility = View.GONE
                option2Btn.visibility = View.GONE
                option3Btn.visibility = View.VISIBLE
                option4Btn.visibility = View.GONE

                // Set center button image to play arrow
                option3Btn.setImageResource(R.drawable.ic_twotone_play_arrow_24)

                displayChapter("Part.$currentChapter")

                if (choiceList[0] == "END") {   // Display last chapter, prompt to try different path,
                    Toast.makeText(baseContext, "Click to try a different path", Toast.LENGTH_LONG).show()
                    option3Btn.setOnClickListener {
                        val theEndToBookCoverIntent = Intent(this, BookCover::class.java)
                        theEndToBookCoverIntent.putExtra("assetsFolder", bookAssets)
                        startActivity(theEndToBookCoverIntent)
                    }
                } else {  // Click on option button 3 to continue to assigned chapter
                    currentChapter = choiceList[0]

                    // Move forward to the next section after clicking option button
                    option3Btn.setOnClickListener {
                        storyDisplay(userRef, currentChapter)
                    }
                }
            }

            2 -> {  // The most frequent occurrence. There are 2 choices for the reader

                // Hide center option button, display left and right option buttons
                option1Btn.visibility = View.VISIBLE
                option2Btn.visibility = View.VISIBLE
                option3Btn.visibility = View.GONE
                option4Btn.visibility = View.GONE

                // Set right button image to "2"
                option2Btn.setImageResource(R.drawable.ic_twotone_looks_two_24)

                // Left button proceeds to first choice.
                option1Btn.setOnClickListener {
                    currentChapter = choiceList[0]
                    storyDisplay(userRef, currentChapter)
                }

                // Right button proceeds to next choice
                option2Btn.setOnClickListener {
                    currentChapter = choiceList[1]
                    storyDisplay(userRef, currentChapter)
                }
            }

            3 -> {
                // Display left, right, and center option buttons
                option1Btn.visibility = View.VISIBLE
                option2Btn.visibility = View.VISIBLE
                option3Btn.visibility = View.VISIBLE
                option4Btn.visibility = View.GONE

                // Rearrange image used for center button as "2" and right button as "3"
                // Right button
                option2Btn.setImageResource(R.drawable.ic_twotone_looks_3_24)
                // Center button
                option3Btn.setImageResource(R.drawable.ic_twotone_looks_two_24)

                // Left button proceeds to choice 1.
                option1Btn.setOnClickListener {
                    currentChapter = choiceList[0]
                    storyDisplay(userRef, currentChapter)
                }

                // Right button proceeds to choice 3
                option2Btn.setOnClickListener {
                    currentChapter = choiceList[2]
                    storyDisplay(userRef, currentChapter)
                }

                // Center button proceeds to choice 2
                option3Btn.setOnClickListener {
                    currentChapter = choiceList[1]
                    storyDisplay(userRef, currentChapter)
                }
            }

            4 -> {
                // Display 4 option buttons, and change the positions for the 2 center buttons
                option1Btn.visibility = View.VISIBLE
                option2Btn.visibility = View.VISIBLE
                option3Btn.visibility = View.VISIBLE
                option4Btn.visibility = View.VISIBLE

                // Set images for buttons 2 thru 4
                // Right button
                option2Btn.setImageResource(R.drawable.ic_twotone_looks_3_24)
                // Top Center button
                option3Btn.setImageResource(R.drawable.ic_twotone_looks_two_24)

                // Left button proceeds to choice 1.
                option1Btn.setOnClickListener {
                    currentChapter = choiceList[0]
                    storyDisplay(userRef, currentChapter)
                }

                // Right button proceeds to choice 3
                option2Btn.setOnClickListener {
                    currentChapter = choiceList[2]
                    storyDisplay(userRef, currentChapter)
                }

                // Center left button proceeds to choice 2
                option3Btn.setOnClickListener {
                    currentChapter = choiceList[1]
                    storyDisplay(userRef, currentChapter)
                }

                // Center right button proceeds to choice 4
                option4Btn.setOnClickListener {
                    currentChapter = choiceList[3]
                    storyDisplay(userRef, currentChapter)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.book_scrolling_toolbar, menu)
        val toolbarBookmark = menu.findItem(R.id.action_bookmark)
        toolbarBookmark?.setOnMenuItemClickListener {
            val bookMarkTool = userRef.child(bookmark)
            bookMarkTool.setValue(currentChapter)
            true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateNavHeader(vol: String) {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)

        val imageView = headerView.findViewById<ImageView>(R.id.headerImageView)
        val headerImageName = assetsDirectory + vol + "_header.jpg"
        val headerImageInputStream: InputStream = assets.open(headerImageName)
        val headerImage = Drawable.createFromStream(headerImageInputStream, null)
        imageView.setImageDrawable(headerImage)

        val headerVolText = headerView.findViewById<TextView>(R.id.headerTextView)
        when (vol) {
            "vol1" -> {
                headerVolText.text = "The Cave of Time"
            }
            "vol2" -> {
                headerVolText.text = "Journey Under the Sea"
            }
            "vol3" -> {
                headerVolText.text = "By Balloon to the Sahara"
            }
            "vol4" -> {
                headerVolText.text = "Space and Beyond"
            }
            "vol5" -> {
                headerVolText.text = "The Mystery of Chimney Rock"
            }
        }
    }

    private fun navigateToDrawerItem(itemId: Int) {
        when (itemId) {
            R.id.nav_home -> {
                val storyToMainIntent = Intent(this, MainActivity::class.java)
                startActivity(storyToMainIntent)
            }
            R.id.nav_fast_forward -> {
                val bookMarkChapter = userRef.child(bookmark)

                bookMarkChapter.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val value = dataSnapshot.getValue(String::class.java)
                        if (value != null) {
                            currentChapter = value
                            storyDisplay(userRef, currentChapter)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle errors here
                    }
                })
            }
            R.id.nav_story_map -> {
                val storyToStoryProgressIntent = Intent(this, StoryProgression::class.java)
                storyToStoryProgressIntent.putExtra("assetsFolder", bookAssets)
                startActivity(storyToStoryProgressIntent)
            }
            R.id.nav_exit -> {
                val storyToBookCoverIntent = Intent(this, StoryLibrary::class.java)
                startActivity(storyToBookCoverIntent)
            }
        }
    }
}